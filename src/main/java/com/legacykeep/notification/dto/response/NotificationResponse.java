package com.legacykeep.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.legacykeep.notification.entity.NotificationChannel;
import com.legacykeep.notification.entity.NotificationPriority;
import com.legacykeep.notification.entity.NotificationStatus;
import com.legacykeep.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for notification information
 * 
 * This DTO provides a clean representation of notification data
 * for API responses, excluding sensitive information.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    // =============================================================================
    // Core Notification Fields
    // =============================================================================

    /**
     * Unique notification ID
     */
    private Long id;

    /**
     * Event ID for tracking
     */
    private String eventId;

    /**
     * Template ID used for this notification
     */
    private String templateId;

    /**
     * Type of notification
     */
    private NotificationType notificationType;

    /**
     * Channel through which notification was sent
     */
    private NotificationChannel channel;

    /**
     * Priority level of the notification
     */
    private NotificationPriority priority;

    /**
     * Current status of the notification
     */
    private NotificationStatus status;

    // =============================================================================
    // Recipient Information
    // =============================================================================

    /**
     * Recipient user ID
     */
    private Long recipientId;

    /**
     * Recipient email (masked for privacy)
     */
    private String recipientEmail;

    /**
     * Recipient phone (masked for privacy)
     */
    private String recipientPhone;

    /**
     * Subject line of the notification
     */
    private String subject;

    // =============================================================================
    // Timing Information
    // =============================================================================

    /**
     * When the notification was created
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    /**
     * When the notification was scheduled to be sent
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime scheduledAt;

    /**
     * When the notification was actually sent
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime sentAt;

    /**
     * When the notification was delivered
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime deliveredAt;

    /**
     * When the notification failed (if applicable)
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime failedAt;

    // =============================================================================
    // Delivery Information
    // =============================================================================

    /**
     * Number of retry attempts made
     */
    private Integer retryCount;

    /**
     * Maximum number of retry attempts allowed
     */
    private Integer maxRetries;

    /**
     * Reason for failure (if applicable)
     */
    private String failureReason;

    // =============================================================================
    // Metadata
    // =============================================================================

    /**
     * Correlation ID for tracking related notifications
     */
    private String correlationId;

    /**
     * Request ID for tracking
     */
    private String requestId;

    /**
     * Additional metadata
     */
    private String metadata;

    // =============================================================================
    // Utility Methods
    // =============================================================================

    /**
     * Check if notification was delivered successfully
     */
    public boolean isDelivered() {
        return status == NotificationStatus.DELIVERED;
    }

    /**
     * Check if notification failed
     */
    public boolean isFailed() {
        return status == NotificationStatus.FAILED;
    }

    /**
     * Check if notification is pending
     */
    public boolean isPending() {
        return status == NotificationStatus.PENDING;
    }

    /**
     * Check if notification is processing
     */
    public boolean isProcessing() {
        return status == NotificationStatus.PROCESSING;
    }

    /**
     * Check if notification was sent
     */
    public boolean isSent() {
        return status == NotificationStatus.SENT;
    }

    /**
     * Check if notification was cancelled
     */
    public boolean isCancelled() {
        return status == NotificationStatus.CANCELLED;
    }

    /**
     * Get delivery duration in milliseconds
     */
    public Long getDeliveryDurationMs() {
        if (sentAt == null || deliveredAt == null) {
            return null;
        }
        return java.time.Duration.between(sentAt, deliveredAt).toMillis();
    }

    /**
     * Get total processing time in milliseconds
     */
    public Long getTotalProcessingTimeMs() {
        if (createdAt == null || deliveredAt == null) {
            return null;
        }
        return java.time.Duration.between(createdAt, deliveredAt).toMillis();
    }

    /**
     * Check if notification can be retried
     */
    public boolean canRetry() {
        return status == NotificationStatus.FAILED && retryCount < maxRetries;
    }
}
