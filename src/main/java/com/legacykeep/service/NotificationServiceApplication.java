package com.legacykeep.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Notification Service Application.
 * 
 * Handles all notification types including email, push notifications,
 * and SMS for the LegacyKeep platform.
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
