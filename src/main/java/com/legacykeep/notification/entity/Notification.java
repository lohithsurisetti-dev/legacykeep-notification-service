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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Notification Entity
 * 
 * Represents a notification in the system. This is the main entity
 * that tracks all notification requests and their status.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_event_id", columnList = "event_id"),
    @Index(name = "idx_notifications_recipient_id", columnList = "recipient_id"),
    @Index(name = "idx_notifications_status", columnList = "status"),
    @Index(name = "idx_notifications_type", columnList = "notification_type"),
    @Index(name = "idx_notifications_created_at", columnList = "created_at"),
    @Index(name = "idx_notifications_scheduled_at", columnList = "scheduled_at"),
    @Index(name = "idx_notifications_template_id", columnList = "template_id")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"content", "templateData", "metadata"})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Event ID is required")
    @Column(name = "event_id", unique = true, nullable = false, length = 255)
    private String eventId;

    @NotNull(message = "Notification type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 20)
    private NotificationType notificationType;

    @NotNull(message = "Template ID is required")
    @Column(name = "template_id", nullable = false, length = 100)
    private String templateId;

    @NotNull(message = "Recipient ID is required")
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "recipient_email", length = 255)
    private String recipientEmail;

    @Column(name = "recipient_phone", length = 20)
    private String recipientPhone;

    @Column(name = "recipient_device_token", length = 500)
    private String recipientDeviceToken;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "content", columnDefinition = "TEXT")
    @JsonIgnore
    private String content;

    @Column(name = "template_data", columnDefinition = "JSONB")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private String templateData; // JSON string for template variables

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private NotificationPriority priority = NotificationPriority.NORMAL;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 3;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "metadata", columnDefinition = "JSONB")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private String metadata; // JSON string for additional metadata

    // =============================================================================
    // Business Logic Methods
    // =============================================================================

    /**
     * Check if the notification can be retried
     */
    @JsonIgnore
    public boolean canRetry() {
        return status == NotificationStatus.FAILED && retryCount < maxRetries;
    }

    /**
     * Check if the notification is ready to be sent
     */
    @JsonIgnore
    public boolean isReadyToSend() {
        if (status != NotificationStatus.PENDING) {
            return false;
        }
        
        if (scheduledAt != null && scheduledAt.isAfter(LocalDateTime.now())) {
            return false;
        }
        
        return true;
    }

    /**
     * Mark notification as sent
     */
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    /**
     * Mark notification as delivered
     */
    public void markAsDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    /**
     * Mark notification as failed
     */
    public void markAsFailed(String reason) {
        this.status = NotificationStatus.FAILED;
        this.failedAt = LocalDateTime.now();
        this.failureReason = reason;
        this.retryCount++;
    }

    /**
     * Mark notification as processing
     */
    public void markAsProcessing() {
        this.status = NotificationStatus.PROCESSING;
    }

    /**
     * Cancel the notification
     */
    public void cancel() {
        this.status = NotificationStatus.CANCELLED;
    }

    /**
     * Get the appropriate recipient identifier based on notification type
     */
    @JsonIgnore
    public String getRecipientIdentifier() {
        switch (notificationType) {
            case EMAIL:
                return recipientEmail;
            case SMS:
                return recipientPhone;
            case PUSH:
                return recipientDeviceToken;
            case IN_APP:
                return recipientId.toString();
            default:
                return null;
        }
    }

    // =============================================================================
    // Constructors
    // =============================================================================

    public Notification(String eventId, NotificationType notificationType, String templateId, Long recipientId) {
        this.eventId = eventId;
        this.notificationType = notificationType;
        this.templateId = templateId;
        this.recipientId = recipientId;
        this.status = NotificationStatus.PENDING;
        this.priority = NotificationPriority.NORMAL;
        this.retryCount = 0;
        this.maxRetries = 3;
    }
}
