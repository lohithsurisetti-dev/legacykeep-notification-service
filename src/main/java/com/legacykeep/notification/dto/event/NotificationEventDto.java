package com.legacykeep.notification.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Dynamic Notification Event DTO for Kafka communication
 * 
 * This DTO is designed to handle any event type dynamically,
 * making it future-proof for growing event requirements.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationEventDto {

    // =============================================================================
    // Core Event Information
    // =============================================================================

    /**
     * Unique event ID
     */
    private String eventId;

    /**
     * Event type (e.g., USER_REGISTERED, PASSWORD_RESET, FAMILY_INVITATION)
     */
    private String eventType;

    /**
     * Event version for backward compatibility
     */
    private String eventVersion = "1.0";

    /**
     * Source service that published the event
     */
    private String sourceService;

    /**
     * Source user ID (if applicable)
     */
    private Long sourceUserId;

    /**
     * Correlation ID for tracking related events
     */
    private String correlationId;

    /**
     * Request ID for tracking
     */
    private String requestId;

    /**
     * Event timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    // =============================================================================
    // Notification Configuration
    // =============================================================================

    /**
     * Template ID to use for this event
     */
    private String templateId;

    /**
     * Notification channels to use (EMAIL, PUSH, SMS, IN_APP)
     * Can be multiple channels for the same event
     */
    private String[] channels;

    /**
     * Priority level (LOW, NORMAL, HIGH, URGENT)
     */
    private String priority = "NORMAL";

    /**
     * Scheduled send time (null for immediate)
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime scheduledAt;

    /**
     * Maximum retry attempts
     */
    private Integer maxRetries = 3;

    // =============================================================================
    // Recipient Information
    // =============================================================================

    /**
     * Recipient user ID
     */
    private Long recipientId;

    /**
     * Recipient email address
     */
    private String recipientEmail;

    /**
     * Recipient phone number
     */
    private String recipientPhone;

    /**
     * Recipient device token
     */
    private String recipientDeviceToken;

    /**
     * Recipient username (for personalization)
     */
    private String recipientUsername;

    /**
     * Recipient first name (for personalization)
     */
    private String recipientFirstName;

    /**
     * Recipient last name (for personalization)
     */
    private String recipientLastName;

    // =============================================================================
    // Dynamic Event Data
    // =============================================================================

    /**
     * Event-specific data (dynamic JSON object)
     * This allows for any event type without changing the DTO structure
     */
    private Map<String, Object> eventData;

    /**
     * Template variables for dynamic content
     * These will be merged with eventData for template processing
     */
    private Map<String, Object> templateVariables;

    /**
     * Custom subject line (overrides template subject)
     */
    private String customSubject;

    /**
     * Custom content (overrides template content)
     */
    private String customContent;

    // =============================================================================
    // Email-Specific Configuration (Primary Focus)
    // =============================================================================

    /**
     * Email sender name
     */
    private String emailSenderName;

    /**
     * Email sender address
     */
    private String emailSenderAddress;

    /**
     * Email reply-to address
     */
    private String emailReplyTo;

    /**
     * Email template type (HTML, TEXT, MJML)
     */
    private String emailTemplateType = "HTML";

    /**
     * Email attachments (base64 encoded)
     */
    private Map<String, String> emailAttachments;

    /**
     * Email headers (custom headers)
     */
    private Map<String, String> emailHeaders;

    // =============================================================================
    // Metadata and Tracking
    // =============================================================================

    /**
     * Additional metadata for tracking
     */
    private Map<String, Object> metadata;

    /**
     * Tags for categorization and filtering
     */
    private String[] tags;

    /**
     * Campaign ID for marketing campaigns
     */
    private String campaignId;

    /**
     * A/B test variant
     */
    private String abTestVariant;

    // =============================================================================
    // Business Logic Methods
    // =============================================================================

    /**
     * Check if event is for email channel
     */
    public boolean isEmailEvent() {
        return channels != null && containsChannel("EMAIL");
    }

    /**
     * Check if event is for push channel
     */
    public boolean isPushEvent() {
        return channels != null && containsChannel("PUSH");
    }

    /**
     * Check if event is for SMS channel
     */
    public boolean isSmsEvent() {
        return channels != null && containsChannel("SMS");
    }

    /**
     * Check if event is for in-app channel
     */
    public boolean isInAppEvent() {
        return channels != null && containsChannel("IN_APP");
    }

    /**
     * Check if event is scheduled
     */
    public boolean isScheduled() {
        return scheduledAt != null && scheduledAt.isAfter(LocalDateTime.now());
    }

    /**
     * Check if event is immediate
     */
    public boolean isImmediate() {
        return scheduledAt == null || scheduledAt.isBefore(LocalDateTime.now());
    }

    /**
     * Check if event is urgent
     */
    public boolean isUrgent() {
        return "URGENT".equalsIgnoreCase(priority);
    }

    /**
     * Get recipient full name
     */
    public String getRecipientFullName() {
        if (recipientFirstName != null && recipientLastName != null) {
            return recipientFirstName + " " + recipientLastName;
        } else if (recipientFirstName != null) {
            return recipientFirstName;
        } else if (recipientUsername != null) {
            return recipientUsername;
        }
        return null;
    }

    /**
     * Get all template variables (merged eventData and templateVariables)
     */
    public Map<String, Object> getAllTemplateVariables() {
        Map<String, Object> allVariables = new java.util.HashMap<>();
        
        if (eventData != null) {
            allVariables.putAll(eventData);
        }
        
        if (templateVariables != null) {
            allVariables.putAll(templateVariables);
        }
        
        // Add common variables
        allVariables.put("recipientId", recipientId);
        allVariables.put("recipientEmail", recipientEmail);
        allVariables.put("recipientUsername", recipientUsername);
        allVariables.put("recipientFirstName", recipientFirstName);
        allVariables.put("recipientLastName", recipientLastName);
        allVariables.put("recipientFullName", getRecipientFullName());
        allVariables.put("eventType", eventType);
        allVariables.put("timestamp", timestamp);
        
        return allVariables;
    }

    /**
     * Check if channel is included
     */
    private boolean containsChannel(String channel) {
        if (channels == null) return false;
        for (String ch : channels) {
            if (channel.equalsIgnoreCase(ch)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate event for email delivery
     */
    public boolean isValidForEmail() {
        return recipientEmail != null && !recipientEmail.trim().isEmpty() && isEmailEvent();
    }

    /**
     * Validate event for push delivery
     */
    public boolean isValidForPush() {
        return recipientDeviceToken != null && !recipientDeviceToken.trim().isEmpty() && isPushEvent();
    }

    /**
     * Validate event for SMS delivery
     */
    public boolean isValidForSms() {
        return recipientPhone != null && !recipientPhone.trim().isEmpty() && isSmsEvent();
    }

    /**
     * Validate event for in-app delivery
     */
    public boolean isValidForInApp() {
        return recipientId != null && isInAppEvent();
    }

    /**
     * Get validation errors for the event
     */
    public java.util.List<String> getValidationErrors() {
        java.util.List<String> errors = new java.util.ArrayList<>();
        
        if (eventId == null || eventId.trim().isEmpty()) {
            errors.add("Event ID is required");
        }
        
        if (eventType == null || eventType.trim().isEmpty()) {
            errors.add("Event type is required");
        }
        
        if (sourceService == null || sourceService.trim().isEmpty()) {
            errors.add("Source service is required");
        }
        
        if (templateId == null || templateId.trim().isEmpty()) {
            errors.add("Template ID is required");
        }
        
        if (channels == null || channels.length == 0) {
            errors.add("At least one channel is required");
        }
        
        if (recipientId == null) {
            errors.add("Recipient ID is required");
        }
        
        // Channel-specific validation
        if (isEmailEvent() && !isValidForEmail()) {
            errors.add("Email channel requires valid recipient email");
        }
        
        if (isPushEvent() && !isValidForPush()) {
            errors.add("Push channel requires valid device token");
        }
        
        if (isSmsEvent() && !isValidForSms()) {
            errors.add("SMS channel requires valid phone number");
        }
        
        if (isInAppEvent() && !isValidForInApp()) {
            errors.add("In-app channel requires valid recipient ID");
        }
        
        return errors;
    }

    /**
     * Check if event is valid
     */
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }
}
