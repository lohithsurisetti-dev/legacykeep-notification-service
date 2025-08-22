package com.legacykeep.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LegacyKeep Notification Service Application
 * 
 * This is the main entry point for the Notification microservice.
 * It handles all notification types including email, push notifications,
 * and SMS through event-driven architecture with Kafka integration.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
