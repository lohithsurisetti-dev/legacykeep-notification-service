package com.legacykeep.notification.entity;

/**
 * Notification Status Enum
 * 
 * Defines the different statuses a notification can have during its lifecycle.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public enum NotificationStatus {
    PENDING,        // Notification created, waiting to be processed
    PROCESSING,     // Notification is currently being processed
    SENT,          // Notification has been sent to the delivery service
    DELIVERED,     // Notification has been delivered to the recipient
    FAILED,        // Notification failed to be sent or delivered
    CANCELLED      // Notification was cancelled before processing
}
