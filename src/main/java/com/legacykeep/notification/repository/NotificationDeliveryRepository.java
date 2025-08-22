package com.legacykeep.notification.repository;

import com.legacykeep.notification.entity.NotificationChannel;
import com.legacykeep.notification.entity.NotificationDelivery;
import com.legacykeep.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for NotificationDelivery entity
 * 
 * Provides data access methods for delivery tracking and management.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Repository
public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {

    // =============================================================================
    // Basic CRUD Operations
    // =============================================================================

    /**
     * Find delivery by notification ID
     */
    Optional<NotificationDelivery> findByNotificationId(Long notificationId);

    /**
     * Check if delivery exists for notification ID
     */
    boolean existsByNotificationId(Long notificationId);

    /**
     * Find deliveries by notification ID
     */
    List<NotificationDelivery> findAllByNotificationId(Long notificationId);

    // =============================================================================
    // Status-based Queries
    // =============================================================================

    /**
     * Find deliveries by status
     */
    List<NotificationDelivery> findByStatus(NotificationStatus status);

    /**
     * Find deliveries by channel
     */
    List<NotificationDelivery> findByChannel(NotificationChannel channel);

    /**
     * Find deliveries by status and channel
     */
    List<NotificationDelivery> findByStatusAndChannel(NotificationStatus status, NotificationChannel channel);

    // =============================================================================
    // Date-based Queries
    // =============================================================================

    /**
     * Find deliveries sent after a specific date
     */
    List<NotificationDelivery> findBySentAtAfter(LocalDateTime date);

    /**
     * Find deliveries delivered after a specific date
     */
    List<NotificationDelivery> findByDeliveredAtAfter(LocalDateTime date);

    /**
     * Find deliveries failed after a specific date
     */
    List<NotificationDelivery> findByFailedAtAfter(LocalDateTime date);

    /**
     * Find deliveries created between two dates
     */
    List<NotificationDelivery> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // =============================================================================
    // Analytics Queries
    // =============================================================================

    /**
     * Count deliveries by status
     */
    @Query("SELECT d.status, COUNT(d) FROM NotificationDelivery d GROUP BY d.status")
    List<Object[]> countByStatus();

    /**
     * Count deliveries by channel
     */
    @Query("SELECT d.channel, COUNT(d) FROM NotificationDelivery d GROUP BY d.channel")
    List<Object[]> countByChannel();

    /**
     * Count deliveries by status and channel
     */
    @Query("SELECT d.status, d.channel, COUNT(d) FROM NotificationDelivery d GROUP BY d.status, d.channel")
    List<Object[]> countByStatusAndChannel();

    /**
     * Get delivery success rate by channel
     */
    @Query("SELECT d.channel, " +
           "COUNT(d) as total, " +
           "COUNT(CASE WHEN d.status = 'DELIVERED' THEN 1 END) as delivered, " +
           "COUNT(CASE WHEN d.status = 'FAILED' THEN 1 END) as failed " +
           "FROM NotificationDelivery d " +
           "GROUP BY d.channel")
    List<Object[]> getDeliveryStatsByChannel();

    // =============================================================================
    // Performance Queries
    // =============================================================================

    /**
     * Get average delivery time by channel
     */
    @Query("SELECT d.channel, " +
           "AVG(EXTRACT(EPOCH FROM (d.deliveredAt - d.sentAt)) * 1000) as avgDeliveryTimeMs " +
           "FROM NotificationDelivery d " +
           "WHERE d.sentAt IS NOT NULL AND d.deliveredAt IS NOT NULL " +
           "GROUP BY d.channel")
    List<Object[]> getAverageDeliveryTimeByChannel();

    /**
     * Get deliveries with delivery time above threshold
     */
    @Query("SELECT d FROM NotificationDelivery d " +
           "WHERE d.sentAt IS NOT NULL AND d.deliveredAt IS NOT NULL " +
           "AND EXTRACT(EPOCH FROM (d.deliveredAt - d.sentAt)) * 1000 > :thresholdMs")
    List<NotificationDelivery> findDeliveriesWithDeliveryTimeAboveThreshold(@Param("thresholdMs") long thresholdMs);

    // =============================================================================
    // Cleanup Queries
    // =============================================================================

    /**
     * Find old delivered deliveries for cleanup
     */
    @Query("SELECT d FROM NotificationDelivery d " +
           "WHERE d.status IN ('DELIVERED', 'FAILED') " +
           "AND d.createdAt < :cutoffDate")
    List<NotificationDelivery> findOldDeliveriesForCleanup(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Delete old delivered deliveries
     */
    @Query("DELETE FROM NotificationDelivery d " +
           "WHERE d.status IN ('DELIVERED', 'FAILED') " +
           "AND d.createdAt < :cutoffDate")
    int deleteOldDeliveries(@Param("cutoffDate") LocalDateTime cutoffDate);

    // =============================================================================
    // Custom Queries
    // =============================================================================

    /**
     * Find successful deliveries for a notification
     */
    @Query("SELECT d FROM NotificationDelivery d " +
           "WHERE d.notificationId = :notificationId " +
           "AND d.status = 'DELIVERED'")
    List<NotificationDelivery> findSuccessfulDeliveriesForNotification(@Param("notificationId") Long notificationId);

    /**
     * Find failed deliveries for a notification
     */
    @Query("SELECT d FROM NotificationDelivery d " +
           "WHERE d.notificationId = :notificationId " +
           "AND d.status = 'FAILED'")
    List<NotificationDelivery> findFailedDeliveriesForNotification(@Param("notificationId") Long notificationId);

    /**
     * Find latest delivery for a notification
     */
    @Query("SELECT d FROM NotificationDelivery d " +
           "WHERE d.notificationId = :notificationId " +
           "ORDER BY d.createdAt DESC")
    List<NotificationDelivery> findLatestDeliveriesForNotification(@Param("notificationId") Long notificationId);
}
