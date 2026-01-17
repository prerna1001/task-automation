package com.example.Task_Scheduler.taskautomation.model;

import java.util.List;
import java.util.Map;

public class Task {
    private String id;
    private String name;
    private String description;
    private String recurrence; // e.g., "daily", "weekly"
    private String actionType; // e.g., "email", "notification"
    private String lastRunAt;
    private String nextRunDate;
    private String userEmail;
    private String deviceToken;
    private String automationType;
    private String templateType;
    private Map<String, Object> payload;
    private List<String> recipients;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRecurrence() { return recurrence; }
    public void setRecurrence(String recurrence) { this.recurrence = recurrence; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getLastRunAt() { return lastRunAt; }
    public void setLastRunAt(String lastRunAt) { this.lastRunAt = lastRunAt; }

    public String getNextRunDate() { return nextRunDate; }
    public void setNextRunDate(String nextRunDate) { this.nextRunDate = nextRunDate; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getDeviceToken() { return deviceToken; }
    public void setDeviceToken(String deviceToken) { this.deviceToken = deviceToken; }

    public String getAutomationType() { return automationType; }
    public void setAutomationType(String automationType) { this.automationType = automationType; }

    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public List<String> getRecipients() { return recipients; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }
}
