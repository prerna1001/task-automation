package com.example.Task_Scheduler.taskautomation.service;

import com.google.cloud.firestore.Firestore;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.example.Task_Scheduler.taskautomation.model.Task;
import com.example.Task_Scheduler.taskautomation.model.TaskRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {
    private static final String TASK_COLLECTION = "tasks";
    private static final String TASK_RUN_COLLECTION = "taskRuns";

    @Autowired(required = false)
    private Firestore firestore;

    private final ConcurrentHashMap<String, Task> inMemoryTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TaskRun> inMemoryTaskRuns = new ConcurrentHashMap<>();

    // Create a new task
    public Task createTask(Task task) throws ExecutionException, InterruptedException {
        if (firestore == null) {
            inMemoryTasks.put(task.getId(), task);
            return task;
        }
        DocumentReference docRef = firestore.collection(TASK_COLLECTION).document(task.getId());
        ApiFuture<com.google.cloud.firestore.WriteResult> result = docRef.set(task);
        result.get();
        return task;
    }

    // Get all tasks
    public List<Task> getAllTasks() throws ExecutionException, InterruptedException {
        if (firestore == null) {
            return new ArrayList<>(inMemoryTasks.values());
        }
        CollectionReference tasksRef = firestore.collection(TASK_COLLECTION);
        ApiFuture<QuerySnapshot> future = tasksRef.get();
        List<Task> tasks = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            tasks.add(doc.toObject(Task.class));
        }
        return tasks;
    }

    // Get a task by ID
    public Task getTaskById(String id) throws ExecutionException, InterruptedException {
        if (firestore == null) {
            return inMemoryTasks.get(id);
        }
        DocumentReference docRef = firestore.collection(TASK_COLLECTION).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot doc = future.get();
        return doc.exists() ? doc.toObject(Task.class) : null;
    }

    // Update a task
    public Task updateTask(String id, Task updatedTask) throws ExecutionException, InterruptedException {
        if (firestore == null) {
            inMemoryTasks.put(id, updatedTask);
            return updatedTask;
        }
        DocumentReference docRef = firestore.collection(TASK_COLLECTION).document(id);
        ApiFuture<com.google.cloud.firestore.WriteResult> result = docRef.set(updatedTask);
        result.get();
        return updatedTask;
    }

    // Delete a task
    public void deleteTask(String id) throws ExecutionException, InterruptedException {
        if (firestore == null) {
            inMemoryTasks.remove(id);
            return;
        }
        DocumentReference docRef = firestore.collection(TASK_COLLECTION).document(id);
        ApiFuture<com.google.cloud.firestore.WriteResult> result = docRef.delete();
        result.get();
    }

    // Get all runs for a task
    public List<TaskRun> getTaskRuns(String taskId) throws ExecutionException, InterruptedException {
        if (firestore == null) {
            List<TaskRun> runs = new ArrayList<>();
            for (TaskRun run : inMemoryTaskRuns.values()) {
                if (taskId.equals(run.getTaskId())) {
                    runs.add(run);
                }
            }
            return runs;
        }
        CollectionReference runsRef = firestore.collection(TASK_RUN_COLLECTION);
        ApiFuture<QuerySnapshot> future = runsRef.whereEqualTo("taskId", taskId).get();
        List<TaskRun> runs = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            runs.add(doc.toObject(TaskRun.class));
        }
        return runs;
    }

    // Log a task run
    public TaskRun logTaskRun(TaskRun run) throws ExecutionException, InterruptedException {
        if (firestore == null) {
            inMemoryTaskRuns.put(run.getId(), run);
            return run;
        }
        DocumentReference docRef = firestore.collection(TASK_RUN_COLLECTION).document(run.getId());
        ApiFuture<com.google.cloud.firestore.WriteResult> result = docRef.set(run);
        result.get();
        return run;
    }
}
