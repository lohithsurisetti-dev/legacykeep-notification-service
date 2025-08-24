package com.legacykeep.notification.repository;

import com.legacykeep.notification.entity.Notification;
import com.legacykeep.notification.entity.NotificationStatus;
import com.legacykeep.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Notification entity
 * 
 * Provides data access methods for notification management including
 * CRUD operations, status queries, and analytics queries.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // =============================================================================
    // Basic CRUD Operations
    // =============================================================================

    /**
     * Find notification by event ID
     */
    Optional<Notification> findByEventId(String eventId);

    /**
     * Check if notification exists by event ID
     */
    boolean existsByEventId(String eventId);

    /**
     * Find notifications by recipient ID
     */
    List<Notification> findByRecipientId(Long recipientId);

    /**
     * Find notifications by recipient ID with pagination
     */
    Page<Notification> findByRecipientId(Long recipientId, Pageable pageable);

    // =============================================================================
    // Status-based Queries
    // =============================================================================

    /**
     * Find notifications by status
     */
    List<Notification> findByStatus(NotificationStatus status);

    /**
     * Find notifications by status with pagination
     */
    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);

    /**
     * Find notifications by recipient ID and status
     */
    List<Notification> findByRecipientIdAndStatus(Long recipientId, NotificationStatus status);

    /**
     * Find notifications by type and status
     */
    List<Notification> findByNotificationTypeAndStatus(NotificationType type, NotificationStatus status);

    /**
     * Find pending notifications ready to be sent
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND (n.scheduledAt IS NULL OR n.scheduledAt <= :now)")
    List<Notification> findPendingNotificationsReadyToSend(@Param("now") LocalDateTime now);

    /**
     * Find failed notifications that can be retried
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < n.maxRetries")
    List<Notification> findFailedNotificationsForRetry();

    // =============================================================================
    // Template-based Queries
    // =============================================================================

    /**
     * Find notifications by template ID
     */
    List<Notification> findByTemplateId(String templateId);

    /**
     * Find notifications by template ID and status
     */
    List<Notification> findByTemplateIdAndStatus(String templateId, NotificationStatus status);

    // =============================================================================
    // Date-based Queries
    // =============================================================================

    /**
     * Find notifications created after a specific date
     */
    List<Notification> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find notifications created between two dates
     */
    List<Notification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find notifications sent after a specific date
     */
    List<Notification> findBySentAtAfter(LocalDateTime date);

    /**
     * Find notifications delivered after a specific date
     */
    List<Notification> findByDeliveredAtAfter(LocalDateTime date);

    /**
     * Find notifications failed after a specific date
     */
    List<Notification> findByFailedAtAfter(LocalDateTime date);

    // =============================================================================
    // Analytics Queries
    // =============================================================================

    /**
     * Count notifications by status
     */
    @Query("SELECT n.status, COUNT(n) FROM Notification n GROUP BY n.status")
    List<Object[]> countByStatus();

    /**
     * Count notifications by type
     */
    @Query("SELECT n.notificationType, COUNT(n) FROM Notification n GROUP BY n.notificationType")
    List<Object[]> countByType();

    /**
     * Count notifications by template
     */
    @Query("SELECT n.templateId, COUNT(n) FROM Notification n GROUP BY n.templateId")
    List<Object[]> countByTemplate();

    /**
     * Get delivery success rate by template
     */
    @Query("SELECT n.templateId, " +
           "COUNT(n) as total, " +
           "COUNT(CASE WHEN n.status = 'DELIVERED' THEN 1 END) as delivered, " +
           "COUNT(CASE WHEN n.status = 'FAILED' THEN 1 END) as failed " +
           "FROM Notification n " +
           "GROUP BY n.templateId")
    List<Object[]> getDeliveryStatsByTemplate();

    /**
     * Get delivery success rate by type
     */
    @Query("SELECT n.notificationType, " +
           "COUNT(n) as total, " +
           "COUNT(CASE WHEN n.status = 'DELIVERED' THEN 1 END) as delivered, " +
           "COUNT(CASE WHEN n.status = 'FAILED' THEN 1 END) as failed " +
           "FROM Notification n " +
           "GROUP BY n.notificationType")
    List<Object[]> getDeliveryStatsByType();

    // =============================================================================
    // Performance Queries
    // =============================================================================

    // TODO: Fix these queries for performance analytics
    // /**
    //  * Find notifications with average delivery time
    //  */
    // @Query("SELECT n.notificationType, " +
    //        "AVG(CAST(EXTRACT(EPOCH FROM (n.deliveredAt - n.sentAt)) AS DOUBLE PRECISION) * 1000) as avgDeliveryTimeMs " +
    //        "FROM Notification n " +
    //        "WHERE n.sentAt IS NOT NULL AND n.deliveredAt IS NOT NULL " +
    //        "GROUP BY n.notificationType")
    // List<Object[]> getAverageDeliveryTimeByType();

    // /**
    //  * Find notifications with average delivery time by template
    //  */
    // @Query("SELECT n.templateId, " +
    //        "AVG(CAST(EXTRACT(EPOCH FROM (n.deliveredAt - n.sentAt)) AS DOUBLE PRECISION) * 1000) as avgDeliveryTimeMs " +
    //        "FROM Notification n " +
    //        "WHERE n.sentAt IS NOT NULL AND n.deliveredAt IS NOT NULL " +
    //        "GROUP BY n.templateId")
    // List<Object[]> getAverageDeliveryTimeByTemplate();

    // =============================================================================
    // Cleanup Queries
    // =============================================================================

    /**
     * Find old notifications for cleanup
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt < :cutoffDate AND n.status IN ('DELIVERED', 'FAILED', 'CANCELLED')")
    List<Notification> findOldNotificationsForCleanup(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Delete old notifications
     */
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate AND n.status IN ('DELIVERED', 'FAILED', 'CANCELLED')")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}






