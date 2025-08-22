package com.legacykeep.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.legacykeep.notification.entity.NotificationChannel;
import com.legacykeep.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for notification template information
 * 
 * This DTO provides a clean representation of notification template data
 * for API responses, including template metadata and content preview.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateResponse {

    // =============================================================================
    // Core Template Fields
    // =============================================================================

    /**
     * Unique template ID
     */
    private Long id;

    /**
     * Template identifier
     */
    private String templateId;

    /**
     * Template name
     */
    private String name;

    /**
     * Template description
     */
    private String description;

    /**
     * Type of notification this template is for
     */
    private NotificationType notificationType;

    /**
     * Channel this template is designed for
     */
    private NotificationChannel channel;

    // =============================================================================
    // Content Information
    // =============================================================================

    /**
     * Subject template (for email templates)
     */
    private String subjectTemplate;

    /**
     * Content template
     */
    private String contentTemplate;

    /**
     * HTML template (for email templates)
     */
    private String htmlTemplate;

    /**
     * Template variables definition (JSON string)
     */
    private String variables;

    // =============================================================================
    // Status and Version
    // =============================================================================

    /**
     * Whether template is active
     */
    private Boolean isActive;

    /**
     * Template version
     */
    private String version;

    // =============================================================================
    // Timestamps
    // =============================================================================

    /**
     * When template was created
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    /**
     * When template was last updated
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    /**
     * Who created the template
     */
    private String createdBy;

    // =============================================================================
    // Content Preview (for display purposes)
    // =============================================================================

    /**
     * Preview of the subject (first 100 characters)
     */
    private String subjectPreview;

    /**
     * Preview of the content (first 200 characters)
     */
    private String contentPreview;

    /**
     * Preview of the HTML content (first 200 characters)
     */
    private String htmlPreview;

    // =============================================================================
    // Template Variables Information
    // =============================================================================

    /**
     * List of required template variables
     */
    private List<String> requiredVariables;

    /**
     * List of optional template variables
     */
    private List<String> optionalVariables;

    /**
     * Total number of variables
     */
    private Integer totalVariables;

    // =============================================================================
    // Utility Methods
    // =============================================================================

    /**
     * Check if template is available for use
     */
    public boolean isAvailable() {
        return isActive && (subjectTemplate != null || contentTemplate != null || htmlTemplate != null);
    }

    /**
     * Check if template has subject content
     */
    public boolean hasSubject() {
        return subjectTemplate != null && !subjectTemplate.trim().isEmpty();
    }

    /**
     * Check if template has text content
     */
    public boolean hasTextContent() {
        return contentTemplate != null && !contentTemplate.trim().isEmpty();
    }

    /**
     * Check if template has HTML content
     */
    public boolean hasHtmlContent() {
        return htmlTemplate != null && !htmlTemplate.trim().isEmpty();
    }

    /**
     * Get the appropriate template content based on channel
     */
    public String getTemplateContent() {
        switch (channel) {
            case EMAIL:
                return htmlTemplate != null ? htmlTemplate : contentTemplate;
            case PUSH:
            case SMS:
            case IN_APP:
                return contentTemplate;
            default:
                return contentTemplate;
        }
    }

    /**
     * Get content preview based on channel
     */
    public String getContentPreviewForChannel() {
        switch (channel) {
            case EMAIL:
                return htmlPreview != null ? htmlPreview : contentPreview;
            case PUSH:
            case SMS:
            case IN_APP:
                return contentPreview;
            default:
                return contentPreview;
        }
    }

    /**
     * Check if template supports multiple content types
     */
    public boolean supportsMultipleContentTypes() {
        int contentTypes = 0;
        if (hasSubject()) contentTypes++;
        if (hasTextContent()) contentTypes++;
        if (hasHtmlContent()) contentTypes++;
        return contentTypes > 1;
    }

    /**
     * Get template content type summary
     */
    public String getContentTypeSummary() {
        StringBuilder summary = new StringBuilder();
        if (hasSubject()) summary.append("Subject, ");
        if (hasTextContent()) summary.append("Text, ");
        if (hasHtmlContent()) summary.append("HTML, ");
        
        if (summary.length() > 0) {
            summary.setLength(summary.length() - 2); // Remove last ", "
        }
        
        return summary.toString();
    }

    /**
     * Check if template is for email notifications
     */
    public boolean isEmailTemplate() {
        return channel == NotificationChannel.EMAIL;
    }

    /**
     * Check if template is for push notifications
     */
    public boolean isPushTemplate() {
        return channel == NotificationChannel.PUSH;
    }

    /**
     * Check if template is for SMS notifications
     */
    public boolean isSmsTemplate() {
        return channel == NotificationChannel.SMS;
    }

    /**
     * Check if template is for in-app notifications
     */
    public boolean isInAppTemplate() {
        return channel == NotificationChannel.IN_APP;
    }
}
