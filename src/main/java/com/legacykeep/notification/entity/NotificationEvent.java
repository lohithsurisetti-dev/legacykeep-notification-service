package com.legacykeep.notification.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Notification Event Entity
 * 
 * Tracks all events received from other services for audit and debugging purposes.
 * This provides a complete audit trail of all notification-related events.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Entity
@Table(name = "notification_events", indexes = {
    @Index(name = "idx_notification_events_event_id", columnList = "event_id"),
    @Index(name = "idx_notification_events_event_type", columnList = "event_type"),
    @Index(name = "idx_notification_events_source_service", columnList = "source_service"),
    @Index(name = "idx_notification_events_created_at", columnList = "created_at"),
    @Index(name = "idx_notification_events_correlation_id", columnList = "correlation_id")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"eventData"})
public class NotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Event ID is required")
    @Column(name = "event_id", unique = true, nullable = false, length = 255)
    private String eventId;

    @NotNull(message = "Event type is required")
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "event_version", length = 20)
    private String eventVersion = "1.0";

    @NotNull(message = "Source service is required")
    @Column(name = "source_service", nullable = false, length = 100)
    private String sourceService;

    @Column(name = "source_user_id")
    private Long sourceUserId;

    @Column(name = "correlation_id", length = 255)
    private String correlationId;

    @Column(name = "request_id", length = 255)
    private String requestId;

    @Column(name = "event_data", columnDefinition = "JSONB")
    private String eventData; // JSON string for event payload

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // =============================================================================
    // Business Logic Methods
    // =============================================================================

    /**
     * Mark event as processed
     */
    public void markAsProcessed() {
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Check if event has been processed
     */
    public boolean isProcessed() {
        return processedAt != null;
    }

    /**
     * Get processing duration in milliseconds
     */
    public Long getProcessingDurationMs() {
        if (processedAt == null) {
            return null;
        }
        return java.time.Duration.between(createdAt, processedAt).toMillis();
    }

    /**
     * Check if event is from a specific service
     */
    public boolean isFromService(String serviceName) {
        return sourceService.equalsIgnoreCase(serviceName);
    }

    /**
     * Check if event is of a specific type
     */
    public boolean isEventType(String type) {
        return eventType.equalsIgnoreCase(type);
    }

    // =============================================================================
    // Constructors
    // =============================================================================

    public NotificationEvent(String eventId, String eventType, String sourceService) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.eventVersion = "1.0";
    }

    public NotificationEvent(String eventId, String eventType, String sourceService, Long sourceUserId, 
                           String correlationId, String requestId, String eventData) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.sourceUserId = sourceUserId;
        this.correlationId = correlationId;
        this.requestId = requestId;
        this.eventData = eventData;
        this.eventVersion = "1.0";
    }
}
