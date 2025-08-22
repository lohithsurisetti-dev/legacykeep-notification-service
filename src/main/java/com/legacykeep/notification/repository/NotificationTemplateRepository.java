package com.legacykeep.notification.repository;

import com.legacykeep.notification.entity.NotificationChannel;
import com.legacykeep.notification.entity.NotificationTemplate;
import com.legacykeep.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for NotificationTemplate entity
 * 
 * Provides data access methods for template management including
 * CRUD operations and template queries by type and channel.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    // =============================================================================
    // Basic CRUD Operations
    // =============================================================================

    /**
     * Find template by template ID
     */
    Optional<NotificationTemplate> findByTemplateId(String templateId);

    /**
     * Check if template exists by template ID
     */
    boolean existsByTemplateId(String templateId);

    /**
     * Find template by template ID and active status
     */
    Optional<NotificationTemplate> findByTemplateIdAndIsActiveTrue(String templateId);

    // =============================================================================
    // Type and Channel Queries
    // =============================================================================

    /**
     * Find templates by notification type
     */
    List<NotificationTemplate> findByNotificationType(NotificationType notificationType);

    /**
     * Find active templates by notification type
     */
    List<NotificationTemplate> findByNotificationTypeAndIsActiveTrue(NotificationType notificationType);

    /**
     * Find templates by channel
     */
    List<NotificationTemplate> findByChannel(NotificationChannel channel);

    /**
     * Find active templates by channel
     */
    List<NotificationTemplate> findByChannelAndIsActiveTrue(NotificationChannel channel);

    /**
     * Find templates by type and channel
     */
    List<NotificationTemplate> findByNotificationTypeAndChannel(NotificationType notificationType, NotificationChannel channel);

    /**
     * Find active templates by type and channel
     */
    List<NotificationTemplate> findByNotificationTypeAndChannelAndIsActiveTrue(NotificationType notificationType, NotificationChannel channel);

    // =============================================================================
    // Version Queries
    // =============================================================================

    /**
     * Find templates by version
     */
    List<NotificationTemplate> findByVersion(String version);

    /**
     * Find templates by template ID and version
     */
    List<NotificationTemplate> findByTemplateIdAndVersion(String templateId, String version);

    /**
     * Find latest version of a template
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.templateId = :templateId ORDER BY t.version DESC")
    List<NotificationTemplate> findLatestVersionsByTemplateId(@Param("templateId") String templateId);

    // =============================================================================
    // Active/Inactive Queries
    // =============================================================================

    /**
     * Find all active templates
     */
    List<NotificationTemplate> findByIsActiveTrue();

    /**
     * Find all inactive templates
     */
    List<NotificationTemplate> findByIsActiveFalse();

    /**
     * Count active templates
     */
    long countByIsActiveTrue();

    /**
     * Count inactive templates
     */
    long countByIsActiveFalse();

    // =============================================================================
    // Search Queries
    // =============================================================================

    /**
     * Find templates by name containing (case-insensitive)
     */
    List<NotificationTemplate> findByNameContainingIgnoreCase(String name);

    /**
     * Find templates by description containing (case-insensitive)
     */
    List<NotificationTemplate> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Find templates by name or description containing (case-insensitive)
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<NotificationTemplate> findByNameOrDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    // =============================================================================
    // Analytics Queries
    // =============================================================================

    /**
     * Count templates by type
     */
    @Query("SELECT t.notificationType, COUNT(t) FROM NotificationTemplate t GROUP BY t.notificationType")
    List<Object[]> countByType();

    /**
     * Count templates by channel
     */
    @Query("SELECT t.channel, COUNT(t) FROM NotificationTemplate t GROUP BY t.channel")
    List<Object[]> countByChannel();

    /**
     * Count templates by type and channel
     */
    @Query("SELECT t.notificationType, t.channel, COUNT(t) FROM NotificationTemplate t GROUP BY t.notificationType, t.channel")
    List<Object[]> countByTypeAndChannel();

    /**
     * Count active templates by type
     */
    @Query("SELECT t.notificationType, COUNT(t) FROM NotificationTemplate t WHERE t.isActive = true GROUP BY t.notificationType")
    List<Object[]> countActiveByType();

    /**
     * Count active templates by channel
     */
    @Query("SELECT t.channel, COUNT(t) FROM NotificationTemplate t WHERE t.isActive = true GROUP BY t.channel")
    List<Object[]> countActiveByChannel();

    // =============================================================================
    // Custom Queries
    // =============================================================================

    /**
     * Find templates that have HTML content
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.htmlTemplate IS NOT NULL AND t.htmlTemplate != ''")
    List<NotificationTemplate> findTemplatesWithHtmlContent();

    /**
     * Find templates that have subject content
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.subjectTemplate IS NOT NULL AND t.subjectTemplate != ''")
    List<NotificationTemplate> findTemplatesWithSubjectContent();

    /**
     * Find templates that have text content
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.contentTemplate IS NOT NULL AND t.contentTemplate != ''")
    List<NotificationTemplate> findTemplatesWithTextContent();

    /**
     * Find templates created by specific user
     */
    List<NotificationTemplate> findByCreatedBy(String createdBy);

    /**
     * Find templates created by specific user and active
     */
    List<NotificationTemplate> findByCreatedByAndIsActiveTrue(String createdBy);
}
