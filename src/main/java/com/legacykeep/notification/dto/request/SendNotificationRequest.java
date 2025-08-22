package com.legacykeep.notification.dto.request;

import com.legacykeep.notification.entity.NotificationChannel;
import com.legacykeep.notification.entity.NotificationPriority;
import com.legacykeep.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Request DTO for sending notifications
 * 
 * This DTO encapsulates all the information needed to send a notification
 * across different channels with proper validation and flexibility.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {

    // =============================================================================
    // Core Notification Fields
    // =============================================================================

    /**
     * Template ID to use for the notification
     */
    @NotBlank(message = "Template ID is required")
    @Size(max = 100, message = "Template ID must not exceed 100 characters")
    private String templateId;

    /**
     * Type of notification to send
     */
    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;

    /**
     * Channel through which to send the notification
     */
    @NotNull(message = "Notification channel is required")
    private NotificationChannel channel;

    /**
     * Priority level of the notification
     */
    private NotificationPriority priority = NotificationPriority.NORMAL;

    // =============================================================================
    // Recipient Information
    // =============================================================================

    /**
     * Recipient user ID
     */
    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    /**
     * Recipient email address (for email notifications)
     */
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String recipientEmail;

    /**
     * Recipient phone number (for SMS notifications)
     */
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String recipientPhone;

    /**
     * Recipient device token (for push notifications)
     */
    @Size(max = 500, message = "Device token must not exceed 500 characters")
    private String recipientDeviceToken;

    // =============================================================================
    // Content Customization
    // =============================================================================

    /**
     * Custom subject line (overrides template subject)
     */
    @Size(max = 255, message = "Subject must not exceed 255 characters")
    private String subject;

    /**
     * Template variables for dynamic content
     */
    private Map<String, Object> templateVariables;

    /**
     * Custom content (overrides template content)
     */
    private String customContent;

    // =============================================================================
    // Scheduling and Delivery
    // =============================================================================

    /**
     * Scheduled send time (null for immediate sending)
     */
    private LocalDateTime scheduledAt;

    /**
     * Maximum number of retry attempts
     */
    private Integer maxRetries = 3;

    // =============================================================================
    // Metadata and Tracking
    // =============================================================================

    /**
     * Correlation ID for tracking related notifications
     */
    @Size(max = 255, message = "Correlation ID must not exceed 255 characters")
    private String correlationId;

    /**
     * Request ID for tracking
     */
    @Size(max = 255, message = "Request ID must not exceed 255 characters")
    private String requestId;

    /**
     * Additional metadata for the notification
     */
    private Map<String, Object> metadata;

    /**
     * Source service that initiated the notification
     */
    @Size(max = 100, message = "Source service must not exceed 100 characters")
    private String sourceService;

    /**
     * Source user ID that initiated the notification
     */
    private Long sourceUserId;

    // =============================================================================
    // Validation Methods
    // =============================================================================

    /**
     * Validate that the request has the appropriate recipient information
     * based on the notification channel
     */
    public boolean isValidForChannel() {
        switch (channel) {
            case EMAIL:
                return recipientEmail != null && !recipientEmail.trim().isEmpty();
            case SMS:
                return recipientPhone != null && !recipientPhone.trim().isEmpty();
            case PUSH:
                return recipientDeviceToken != null && !recipientDeviceToken.trim().isEmpty();
            case IN_APP:
                return recipientId != null;
            default:
                return false;
        }
    }

    /**
     * Get the appropriate recipient identifier for the channel
     */
    public String getRecipientIdentifier() {
        switch (channel) {
            case EMAIL:
                return recipientEmail;
            case SMS:
                return recipientPhone;
            case PUSH:
                return recipientDeviceToken;
            case IN_APP:
                return recipientId != null ? recipientId.toString() : null;
            default:
                return null;
        }
    }

    /**
     * Check if this is a scheduled notification
     */
    public boolean isScheduled() {
        return scheduledAt != null && scheduledAt.isAfter(LocalDateTime.now());
    }

    /**
     * Check if this is an immediate notification
     */
    public boolean isImmediate() {
        return scheduledAt == null || scheduledAt.isBefore(LocalDateTime.now());
    }
}
