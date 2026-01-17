package com.example.Task_Scheduler.taskautomation.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id:}")
    private String projectId;

    @Bean
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
    public Firestore firestore() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            GoogleCredentials creds;
            String credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            if (credPath != null && !credPath.isEmpty()) {
                creds = GoogleCredentials.fromStream(new FileInputStream(credPath));
            } else {
                creds = GoogleCredentials.getApplicationDefault();
            }
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setCredentials(creds);
            if (projectId != null && !projectId.isEmpty()) {
                optionsBuilder.setProjectId(projectId);
            }
            FirebaseApp.initializeApp(optionsBuilder.build());
        }
        return FirestoreClient.getFirestore();
    }
}
