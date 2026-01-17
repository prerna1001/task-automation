package com.example.Task_Scheduler.taskautomation.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Personalization;
import java.util.List;

// Removed unused import for javax.swing.text.AbstractDocument.Content

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    public void sendEmail(String to, String subject, String body){
        Email from = new Email("shindeprerna1012@gmail.com");
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
        try{
                request.setMethod(com.sendgrid.Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("SendGrid response: "+response.getStatusCode());
        }catch(Exception ex){
            ex.printStackTrace();
        }

        try{
                request.setMethod(com.sendgrid.Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("SendGrid response: "+response.getStatusCode());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    //send email to multiple recipients (bulk) in one API call,
    //but ensure each recipient only sees their own address.
    public void sendBulkEmail(List<String> recipents, String subject, String body){
        if (recipents == null || recipents.isEmpty()) {
            return;
        }

        Email from = new Email("shindeprerna1012@gmail.com");
        Content content = new Content("text/plain", body);
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addContent(content);

        for (String to : recipents) {
            if (to == null || to.trim().isEmpty()) {
                continue;
            }
            Personalization personalization = new Personalization();
            personalization.addTo(new Email(to.trim()));
            mail.addPersonalization(personalization);
        }

        try{
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(com.sendgrid.Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("SendGrid bulk response: " + response.getStatusCode());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

     // Template methods for automation types
    public void sendDailyPromo(List<String> recipients, String body) {
        sendBulkEmail(recipients, "Daily Promotional Email", body);
    }

    public void sendWeeklyPromo(List<String> recipients, String body) {
        sendBulkEmail(recipients, "Weekly Promotional Email", body);
    }

    public void sendWeeklySummary(List<String> recipients, String body) {
        sendBulkEmail(recipients, "Weekly Summary Email", body);
    }

    public void sendMonthlySummary(List<String> recipients, String body) {
        sendBulkEmail(recipients, "Monthly Summary Email", body);
    }

    // Follow-up emails to a single recipient
    public void sendFollowUp(String to, String body, int days) {
        sendEmail(to, days + "-Day Follow-Up Email", body);
    }

}