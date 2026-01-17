package com.example.Task_Scheduler.taskautomation.dto;

import java.util.List;
import java.util.Map;

public class AutomationRequest {
    private List<String> recipients; // email addresses or phone numbers
    private String templateType;
    private Map<String, String> payload;

    public List<String> getRecipients() {
        return recipients;
    }
    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }
    public String getTemplateType() {
        return templateType;
    }
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
    public Map<String, String> getPayload() {
        return payload;
    }
    public void setPayload(Map<String, String> payload) {
        this.payload = payload;
    }
}
