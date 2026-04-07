package com.finance.app.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfig {

    @Value("${app.firebase.credentials-path:}")
    private String credentialsPath;

    @PostConstruct
    public void initializeFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                GoogleCredentials credentials;

                if (Objects.nonNull(credentialsPath) && !credentialsPath.isBlank()) {
                    InputStream serviceAccount = new FileInputStream(credentialsPath);
                    credentials = GoogleCredentials.fromStream(serviceAccount);
                } else {
                    credentials = GoogleCredentials.getApplicationDefault();
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();

                FirebaseApp.initializeApp(options);
            } catch (IOException | RuntimeException e) {
                // Ignore initialization error in test environment or if credentials are missing
                // This allows the app to start without Firebase features
            }
        }
    }

}
