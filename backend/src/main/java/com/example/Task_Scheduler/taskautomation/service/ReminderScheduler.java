package com.example.Task_Scheduler.taskautomation.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

import com.example.Task_Scheduler.taskautomation.model.Task;
import com.example.Task_Scheduler.taskautomation.model.TaskRun;

@Component
public class ReminderScheduler {
    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);
    private final TaskService service;
    private final ScheduledExecutorService scheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    public ReminderScheduler(@Lazy TaskService service) {
        this.service = service;
        this.scheduler = Executors.newScheduledThreadPool(10);
        this.scheduledTasks = new ConcurrentHashMap<>();
    }

    // When app starts, load existing tasks and schedule them
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws ExecutionException, InterruptedException {
        log.info("Loading existing tasks and scheduling reminders");
        java.util.List<Task> tasks = service.getAllTasks();
        for (Task task : tasks) {
            scheduleTask(task);
        }
        log.info("Scheduled {} existing tasks", tasks.size());
    }

    // Public method for external scheduling requests
    public void scheduleTask(String taskId) throws ExecutionException, InterruptedException {
        Task task = service.getTaskById(taskId);
        if (task != null) {
            scheduleTask(task);
        }
    }

    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null && !future.isDone()) {
            future.cancel(false);
            log.debug("Cancelled scheduled execution for task {}", taskId);
        }
    }

    // Schedule a task to run at its nextRunDate
    public void scheduleTask(Task task) {
        if (task.getNextRunDate() == null) {
            log.warn("Task '{}' has no nextRunDate, skipping", task.getName());
            return;
        }
        cancelTask(task.getId());
        Instant now = Instant.now();
        Instant nextRunAt = Instant.parse(task.getNextRunDate());
        long delayMillis = Duration.between(now, nextRunAt).toMillis();
        if (delayMillis < 0) {
            log.info("Task '{}' is overdue, triggering now", task.getName());
            triggerTaskReminder(task);
        }
        ScheduledFuture<?> future = scheduler.schedule(
            () -> triggerTaskReminder(task),
            Math.max(delayMillis, 0),
            TimeUnit.MILLISECONDS
        );
        scheduledTasks.put(task.getId(), future);
        log.info("Scheduled task '{}' to trigger in {} ms (at {})", task.getName(), delayMillis, task.getNextRunDate());
    }

    // Trigger the reminder and reschedule
    private void triggerTaskReminder(Task task) {
        try {
            log.info("REMINDER: Task '{}' (ID: {}) is due!", task.getName(), task.getId());

            // --- AUTOMATION LOGIC FOR EACH TASK TYPE ---
            String automationType = task.getAutomationType(); // EMAIL, NOTIFICATION, TASK_RESET
            String templateType = task.getTemplateType(); // e.g., "Daily promotional email"
            if (automationType != null && templateType != null) {
                switch (automationType) {
                                        case "EMAIL":
                                                // Email automations
                                                switch (templateType) {
                                                        case "Daily reminder":
                                                        case "Daily promotional email":
                                                                log.info("[EMAIL] Sending daily reminder/promotional email for task '{}'", task.getName());
                                                                    emailService.sendDailyPromo(task.getRecipients(), (String) task.getPayload().get("body"));
                                                                break;
                            case "Weekly promotional email":
                                log.info("[EMAIL] Sending weekly promotional email for task '{}'", task.getName());
                                  emailService.sendWeeklyPromo(task.getRecipients(), (String) task.getPayload().get("body"));
                                break;
                            case "Weekly summary email":
                                log.info("[EMAIL] Sending weekly summary email for task '{}'", task.getName());
                                  emailService.sendWeeklySummary(task.getRecipients(), (String) task.getPayload().get("body"));
                                break;
                            case "Monthly summary email":
                                log.info("[EMAIL] Sending monthly summary email for task '{}'", task.getName());
                                  emailService.sendMonthlySummary(task.getRecipients(), (String) task.getPayload().get("body"));
                                break;
                            case "3-day follow-up email":
                                log.info("[EMAIL] Sending 3-day follow-up email for task '{}'", task.getName());
                                   emailService.sendFollowUp(task.getUserEmail(), (String) task.getPayload().get("body"), 3);
                                break;
                            case "7-day follow-up email":
                                log.info("[EMAIL] Sending 7-day follow-up email for task '{}'", task.getName());
                                  emailService.sendFollowUp(task.getUserEmail(), (String) task.getPayload().get("body"), 7);
                                break;
                            case "14-day follow-up email":
                                log.info("[EMAIL] Sending 14-day follow-up email for task '{}'", task.getName());
                                  emailService.sendFollowUp(task.getUserEmail(), (String) task.getPayload().get("body"), 14);
                                break;
                            default:
                                log.warn("Unknown email templateType '{}' for task '{}'", templateType, task.getName());
                        }
                        break;
                    case "NOTIFICATION":
                        // Notification automations
                        switch (templateType) {
                            case "Daily check-in notification":
                                log.info("[NOTIFICATION] Sending daily check-in notification for task '{}'", task.getName());
                                  notificationService.sendDailyCheckIn(task.getDeviceToken(), (String) task.getPayload().get("message"));
                                break;
                            case "Daily insights notification":
                                log.info("[NOTIFICATION] Sending daily insights notification for task '{}'", task.getName());
                                  notificationService.sendDailyInsights(task.getDeviceToken(), (String) task.getPayload().get("message"));
                                break;
                            case "Weekly insights notification":
                                log.info("[NOTIFICATION] Sending weekly insights notification for task '{}'", task.getName());
                                  notificationService.sendWeeklyInsights(task.getDeviceToken(), (String) task.getPayload().get("message"));
                                break;
                            case "Notify user if a task is incomplete for 3 days":
                                log.info("[NOTIFICATION] Notifying user of incomplete task for 3 days: '{}'", task.getName());
                                  notificationService.notifyIncompleteTask(task.getDeviceToken(), (String) task.getPayload().get("message"), 3);
                                break;
                            default:
                                log.warn("Unknown notification templateType '{}' for task '{}'", templateType, task.getName());
                        }
                        break;
                    }
            } else {
                log.warn("No automationType or templateType defined for task '{}'", task.getName());
            }

            // Log the run
            TaskRun run = new TaskRun();
            run.setId(java.util.UUID.randomUUID().toString());
            run.setTaskId(task.getId());
            run.setRunAt(java.time.LocalDateTime.now().toString());
            run.setStatus("success");
            service.logTaskRun(run);

            // Advance schedule to next occurrence (customize for recurrence)
            Instant now = Instant.now();
            Instant nextRun = now.plus(Duration.ofDays(1)); // Or use your recurrence logic
            task.setLastRunAt(now.toString());
            task.setNextRunDate(nextRun.toString());
            service.updateTask(task.getId(), task);

            // Reschedule for next occurrence
            scheduleTask(task);
        } catch (Exception e) {
            log.error("Error processing reminder for task {}: {}", task.getId(), e.getMessage());
        }
    }

    // Call this when a task is updated/created from TaskService
    public void rescheduleTask(String taskId) throws ExecutionException, InterruptedException {
        Task task = service.getTaskById(taskId);
        if (task != null) {
            scheduleTask(task);
        }
    }

    // Cleanup on shutdown
    public void shutdown() {
        log.info("Shutting down task scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
