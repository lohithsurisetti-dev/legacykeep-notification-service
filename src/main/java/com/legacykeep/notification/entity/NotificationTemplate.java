package com.legacykeep.notification.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Notification Template Entity
 * 
 * Represents a notification template that can be used to generate
 * notifications with consistent formatting and content.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Entity
@Table(name = "notification_templates", indexes = {
    @Index(name = "idx_notification_templates_template_id", columnList = "template_id"),
    @Index(name = "idx_notification_templates_type", columnList = "notification_type"),
    @Index(name = "idx_notification_templates_active", columnList = "is_active")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"subjectTemplate", "contentTemplate", "htmlTemplate", "variables"})
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Template ID is required")
    @Column(name = "template_id", unique = true, nullable = false, length = 100)
    private String templateId;

    @NotNull(message = "Template name is required")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Notification type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 20)
    private NotificationType notificationType;

    @NotNull(message = "Channel is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(name = "subject_template", columnDefinition = "TEXT")
    @JsonIgnore
    private String subjectTemplate;

    @Column(name = "content_template", columnDefinition = "TEXT")
    @JsonIgnore
    private String contentTemplate;

    @Column(name = "html_template", columnDefinition = "TEXT")
    @JsonIgnore
    private String htmlTemplate;

    @Column(name = "variables", columnDefinition = "JSONB")
    private String variables; // JSON string for template variables definition

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "version", length = 20)
    private String version = "1.0";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    // =============================================================================
    // Business Logic Methods
    // =============================================================================

    /**
     * Check if the template is available for use
     */
    @JsonIgnore
    public boolean isAvailable() {
        return isActive && (subjectTemplate != null || contentTemplate != null || htmlTemplate != null);
    }

    /**
     * Get the appropriate template content based on channel
     */
    @JsonIgnore
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
     * Check if template has subject (mainly for email templates)
     */
    @JsonIgnore
    public boolean hasSubject() {
        return subjectTemplate != null && !subjectTemplate.trim().isEmpty();
    }

    /**
     * Check if template has HTML content (mainly for email templates)
     */
    @JsonIgnore
    public boolean hasHtmlContent() {
        return htmlTemplate != null && !htmlTemplate.trim().isEmpty();
    }

    /**
     * Check if template has plain text content
     */
    @JsonIgnore
    public boolean hasTextContent() {
        return contentTemplate != null && !contentTemplate.trim().isEmpty();
    }

    // =============================================================================
    // Constructors
    // =============================================================================

    public NotificationTemplate(String templateId, String name, NotificationType notificationType, NotificationChannel channel) {
        this.templateId = templateId;
        this.name = name;
        this.notificationType = notificationType;
        this.channel = channel;
        this.isActive = true;
        this.version = "1.0";
    }

    public NotificationTemplate(String templateId, String name, String description, NotificationType notificationType, 
                              NotificationChannel channel, String subjectTemplate, String contentTemplate, String htmlTemplate) {
        this.templateId = templateId;
        this.name = name;
        this.description = description;
        this.notificationType = notificationType;
        this.channel = channel;
        this.subjectTemplate = subjectTemplate;
        this.contentTemplate = contentTemplate;
        this.htmlTemplate = htmlTemplate;
        this.isActive = true;
        this.version = "1.0";
    }
}
