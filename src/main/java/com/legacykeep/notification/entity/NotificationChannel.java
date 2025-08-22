package com.legacykeep.notification.entity;

/**
 * Notification Channel Enum
 * 
 * Defines the different channels through which notifications can be sent.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public enum NotificationChannel {
    EMAIL,      // Email notifications via SMTP
    PUSH,       // Push notifications via FCM/APNS
    SMS,        // SMS notifications via SMS gateway
    IN_APP      // In-app notifications within the application
}
