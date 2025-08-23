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
 * Notification Delivery Entity
 * 
 * Tracks the delivery status of notifications across different channels.
 * Each notification can have multiple delivery attempts across different channels.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Entity
@Table(name = "notification_deliveries", indexes = {
    @Index(name = "idx_notification_deliveries_notification_id", columnList = "notification_id"),
    @Index(name = "idx_notification_deliveries_status", columnList = "status"),
    @Index(name = "idx_notification_deliveries_channel", columnList = "channel"),
    @Index(name = "idx_notification_deliveries_sent_at", columnList = "sent_at")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"deliveryMetadata"})
public class NotificationDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Notification ID is required")
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @NotNull(message = "Channel is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "delivery_metadata", columnDefinition = "JSONB")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private String deliveryMetadata; // JSON string for delivery-specific metadata

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // =============================================================================
    // Business Logic Methods
    // =============================================================================

    /**
     * Mark delivery as sent
     */
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    /**
     * Mark delivery as delivered
     */
    public void markAsDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    /**
     * Mark delivery as failed
     */
    public void markAsFailed(String reason) {
        this.status = NotificationStatus.FAILED;
        this.failedAt = LocalDateTime.now();
        this.failureReason = reason;
    }

    /**
     * Mark delivery as processing
     */
    public void markAsProcessing() {
        this.status = NotificationStatus.PROCESSING;
    }

    /**
     * Check if delivery was successful
     */
    @JsonIgnore
    public boolean isSuccessful() {
        return status == NotificationStatus.DELIVERED;
    }

    /**
     * Check if delivery failed
     */
    @JsonIgnore
    public boolean isFailed() {
        return status == NotificationStatus.FAILED;
    }

    /**
     * Check if delivery is pending
     */
    @JsonIgnore
    public boolean isPending() {
        return status == NotificationStatus.PENDING;
    }

    /**
     * Check if delivery is processing
     */
    @JsonIgnore
    public boolean isProcessing() {
        return status == NotificationStatus.PROCESSING;
    }

    /**
     * Get delivery duration in milliseconds
     */
    @JsonIgnore
    public Long getDeliveryDurationMs() {
        if (sentAt == null || deliveredAt == null) {
            return null;
        }
        return java.time.Duration.between(sentAt, deliveredAt).toMillis();
    }

    // =============================================================================
    // Constructors
    // =============================================================================

    public NotificationDelivery(Long notificationId, NotificationChannel channel) {
        this.notificationId = notificationId;
        this.channel = channel;
        this.status = NotificationStatus.PENDING;
    }

    public NotificationDelivery(Long notificationId, NotificationChannel channel, NotificationStatus status) {
        this.notificationId = notificationId;
        this.channel = channel;
        this.status = status;
    }
}
