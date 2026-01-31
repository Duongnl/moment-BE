package com.moment.moment_BE.service;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FCMInitializer {

    @Value("${firebase.type}")
    String type;

    @Value("${firebase.client-id}")
    String clientId;

    @Value("${firebase.client-email}")
    String clientEmail;

    @Value("${firebase.auth-uri}")
    String authUri;

    @Value("${firebase.project-id}")
    String projectId;

    @Value("${firebase.auth-provider-x509-cert-url}")
    String authProviderCertUrl;

    @Value("${firebase.client-x509-cert-url}")
    String clientCertUrl;

    @Value("${firebase.private-key}")
    String privateKey;

    @Value("${firebase.private-key-id}")
    String privateKeyId;

    @Value("${firebase.token-uri}")
    String tokenUri;

    @Value("${firebase.universe-domain}")
    String universeDomain;


    @PostConstruct
    public void initialize() {
        try {
            String serviceAccountJson = buildServiceAccountJsonFromEnv();

            ByteArrayInputStream credentialsStream = new ByteArrayInputStream(
                    serviceAccountJson.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("FirebaseApp initialized.");
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo Firebase: " + e.getMessage());
        }
    }

    private String buildServiceAccountJsonFromEnv() {

        return "{"
                + "\"type\": \"" + type + "\","
                + "\"project_id\": \"" + projectId + "\","
                + "\"private_key_id\": \"" + privateKeyId + "\","
                + "\"private_key\": \"" + privateKey + "\","
                + "\"client_email\": \"" + clientEmail + "\","
                + "\"client_id\": \"" + clientId + "\","
                + "\"auth_uri\": \"" + authUri + "\","
                + "\"token_uri\": \"" + tokenUri + "\","
                + "\"auth_provider_x509_cert_url\": \"" + authProviderCertUrl + "\","
                + "\"client_x509_cert_url\": \"" + clientCertUrl + "\","
                + "\"universe_domain\": \"" + universeDomain + "\""
                + "}";
    }
}
