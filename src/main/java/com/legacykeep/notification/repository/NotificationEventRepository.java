package com.legacykeep.notification.repository;

import com.legacykeep.notification.entity.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for NotificationEvent entity
 * 
 * Provides data access methods for event tracking and audit purposes.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {

    // =============================================================================
    // Basic CRUD Operations
    // =============================================================================

    /**
     * Find event by event ID
     */
    Optional<NotificationEvent> findByEventId(String eventId);

    /**
     * Check if event exists by event ID
     */
    boolean existsByEventId(String eventId);

    /**
     * Find events by source service
     */
    List<NotificationEvent> findBySourceService(String sourceService);

    /**
     * Find events by event type
     */
    List<NotificationEvent> findByEventType(String eventType);

    /**
     * Find events by source user ID
     */
    List<NotificationEvent> findBySourceUserId(Long sourceUserId);

    // =============================================================================
    // Processing Status Queries
    // =============================================================================

    /**
     * Find processed events
     */
    List<NotificationEvent> findByProcessedAtIsNotNull();

    /**
     * Find unprocessed events
     */
    List<NotificationEvent> findByProcessedAtIsNull();

    /**
     * Find events processed after a specific date
     */
    List<NotificationEvent> findByProcessedAtAfter(LocalDateTime date);

    /**
     * Find events processed between two dates
     */
    List<NotificationEvent> findByProcessedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // =============================================================================
    // Date-based Queries
    // =============================================================================

    /**
     * Find events created after a specific date
     */
    List<NotificationEvent> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find events created between two dates
     */
    List<NotificationEvent> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find events created today
     */
    @Query("SELECT e FROM NotificationEvent e WHERE DATE(e.createdAt) = CURRENT_DATE")
    List<NotificationEvent> findEventsCreatedToday();

    /**
     * Find events created in the last 24 hours
     */
    @Query("SELECT e FROM NotificationEvent e WHERE e.createdAt >= :twentyFourHoursAgo")
    List<NotificationEvent> findEventsInLast24Hours(@Param("twentyFourHoursAgo") LocalDateTime twentyFourHoursAgo);

    // =============================================================================
    // Correlation and Request Queries
    // =============================================================================

    /**
     * Find events by correlation ID
     */
    List<NotificationEvent> findByCorrelationId(String correlationId);

    /**
     * Find events by request ID
     */
    List<NotificationEvent> findByRequestId(String requestId);

    /**
     * Find events by correlation ID and source service
     */
    List<NotificationEvent> findByCorrelationIdAndSourceService(String correlationId, String sourceService);

    // =============================================================================
    // Combined Queries
    // =============================================================================

    /**
     * Find events by source service and event type
     */
    List<NotificationEvent> findBySourceServiceAndEventType(String sourceService, String eventType);

    /**
     * Find events by source service and processing status
     */
    @Query("SELECT e FROM NotificationEvent e WHERE e.sourceService = :sourceService AND e.processedAt IS :processed")
    List<NotificationEvent> findBySourceServiceAndProcessingStatus(
            @Param("sourceService") String sourceService, 
            @Param("processed") LocalDateTime processed
    );

    /**
     * Find events by event type and processing status
     */
    @Query("SELECT e FROM NotificationEvent e WHERE e.eventType = :eventType AND e.processedAt IS :processed")
    List<NotificationEvent> findByEventTypeAndProcessingStatus(
            @Param("eventType") String eventType, 
            @Param("processed") LocalDateTime processed
    );

    // =============================================================================
    // Analytics Queries
    // =============================================================================

    /**
     * Count events by event type
     */
    @Query("SELECT e.eventType, COUNT(e) FROM NotificationEvent e GROUP BY e.eventType")
    List<Object[]> countByEventType();

    /**
     * Count events by source service
     */
    @Query("SELECT e.sourceService, COUNT(e) FROM NotificationEvent e GROUP BY e.sourceService")
    List<Object[]> countBySourceService();

    /**
     * Count events by processing status
     */
    @Query("SELECT CASE WHEN e.processedAt IS NULL THEN 'UNPROCESSED' ELSE 'PROCESSED' END, COUNT(e) FROM NotificationEvent e GROUP BY CASE WHEN e.processedAt IS NULL THEN 'UNPROCESSED' ELSE 'PROCESSED' END")
    List<Object[]> countByProcessingStatus();

    /**
     * Count events by event type and processing status
     */
    @Query("SELECT e.eventType, CASE WHEN e.processedAt IS NULL THEN 'UNPROCESSED' ELSE 'PROCESSED' END, COUNT(e) FROM NotificationEvent e GROUP BY e.eventType, CASE WHEN e.processedAt IS NULL THEN 'UNPROCESSED' ELSE 'PROCESSED' END")
    List<Object[]> countByEventTypeAndProcessingStatus();

    /**
     * Count events by source service and processing status
     */
    @Query("SELECT e.sourceService, CASE WHEN e.processedAt IS NULL THEN 'UNPROCESSED' ELSE 'PROCESSED' END, COUNT(e) FROM NotificationEvent e GROUP BY e.sourceService, CASE WHEN e.processedAt IS NULL THEN 'UNPROCESSED' ELSE 'PROCESSED' END")
    List<Object[]> countBySourceServiceAndProcessingStatus();

    // =============================================================================
    // Performance Queries
    // =============================================================================

    /**
     * Get average processing time by event type
     */
    @Query("SELECT e.eventType, AVG(EXTRACT(EPOCH FROM (e.processedAt - e.createdAt)) * 1000) as avgProcessingTimeMs FROM NotificationEvent e WHERE e.processedAt IS NOT NULL GROUP BY e.eventType")
    List<Object[]> getAverageProcessingTimeByEventType();

    /**
     * Get average processing time by source service
     */
    @Query("SELECT e.sourceService, AVG(EXTRACT(EPOCH FROM (e.processedAt - e.createdAt)) * 1000) as avgProcessingTimeMs FROM NotificationEvent e WHERE e.processedAt IS NOT NULL GROUP BY e.sourceService")
    List<Object[]> getAverageProcessingTimeBySourceService();

    /**
     * Get events with processing time above threshold
     */
    @Query("SELECT e FROM NotificationEvent e WHERE e.processedAt IS NOT NULL AND EXTRACT(EPOCH FROM (e.processedAt - e.createdAt)) * 1000 > :thresholdMs")
    List<NotificationEvent> findEventsWithProcessingTimeAboveThreshold(@Param("thresholdMs") long thresholdMs);

    // =============================================================================
    // Cleanup Queries
    // =============================================================================

    /**
     * Find old processed events for cleanup
     */
    @Query("SELECT e FROM NotificationEvent e WHERE e.processedAt IS NOT NULL AND e.createdAt < :cutoffDate")
    List<NotificationEvent> findOldProcessedEventsForCleanup(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Delete old processed events
     */
    @Query("DELETE FROM NotificationEvent e WHERE e.processedAt IS NOT NULL AND e.createdAt < :cutoffDate")
    int deleteOldProcessedEvents(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find duplicate events (same event ID)
     */
    @Query("SELECT e.eventId, COUNT(e) FROM NotificationEvent e GROUP BY e.eventId HAVING COUNT(e) > 1")
    List<Object[]> findDuplicateEventIds();

    // =============================================================================
    // Custom Queries
    // =============================================================================

    /**
     * Find events from specific services in a time range
     */
    @Query("SELECT e FROM NotificationEvent e WHERE e.sourceService IN :services AND e.createdAt BETWEEN :startDate AND :endDate")
    List<NotificationEvent> findEventsFromServicesInTimeRange(
            @Param("services") List<String> services,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find events with specific event types in a time range
     */
    @Query("SELECT e FROM NotificationEvent e WHERE e.eventType IN :eventTypes AND e.createdAt BETWEEN :startDate AND :endDate")
    List<NotificationEvent> findEventsWithTypesInTimeRange(
            @Param("eventTypes") List<String> eventTypes,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
