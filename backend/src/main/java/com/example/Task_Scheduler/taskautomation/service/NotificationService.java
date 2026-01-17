package com.example.Task_Scheduler.taskautomation.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    //send generic notification
    public void sendNotification(String deviceToken, String title, String body){
        Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .build();
        try{
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM response: " + response);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Daily check-in notification
    public void sendDailyCheckIn(String deviceToken, String message) {
        sendNotification(deviceToken, "Daily Check-In", message);
    }

    // Daily insights notification
    public void sendDailyInsights(String deviceToken, String message) {
        sendNotification(deviceToken, "Daily Insights", message);
    }

    // Weekly insights notification
    public void sendWeeklyInsights(String deviceToken, String message) {
        sendNotification(deviceToken, "Weekly Insights", message);
    }

    // Notify user if a task is incomplete for N days
    public void notifyIncompleteTask(String deviceToken, String message, int days) {
        sendNotification(deviceToken, "Task Incomplete for " + days + " Days", message);
    }
}
