package com.example.Task_Scheduler.taskautomation.model;

public class TaskRun {
    private String id;
    private String taskId;
    private String runAt;
    private String status; // "success", "failed"

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getRunAt() { return runAt; }
    public void setRunAt(String runAt) { this.runAt = runAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}