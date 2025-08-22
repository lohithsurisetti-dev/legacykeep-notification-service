package com.legacykeep.notification.service;

import com.legacykeep.notification.dto.request.SendNotificationRequest;
import com.legacykeep.notification.dto.response.NotificationResponse;
import com.legacykeep.notification.entity.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Notification Service Interface
 * 
 * Defines the contract for notification operations including
 * notification creation, management, and delivery tracking.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface NotificationService {

    // =============================================================================
    // Notification Creation and Management
    // =============================================================================

    /**
     * Create and send a notification.
     * 
     * @param request Notification request data
     * @return Notification response with details
     */
    NotificationResponse sendNotification(SendNotificationRequest request);

    /**
     * Get notification by ID.
     * 
     * @param id Notification ID
     * @return Notification response
     */
    NotificationResponse getNotificationById(Long id);

    /**
     * Get notification by event ID.
     * 
     * @param eventId Event ID
     * @return Notification response
     */
    NotificationResponse getNotificationByEventId(String eventId);

    /**
     * Get notifications for a user with pagination.
     * 
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return Page of notification responses
     */
    Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable);

    /**
     * Get notifications by status.
     * 
     * @param status Notification status
     * @return List of notification responses
     */
    List<NotificationResponse> getNotificationsByStatus(NotificationStatus status);

    /**
     * Cancel a notification.
     * 
     * @param id Notification ID
     * @return Notification response
     */
    NotificationResponse cancelNotification(Long id);

    /**
     * Retry a failed notification.
     * 
     * @param id Notification ID
     * @return Notification response
     */
    NotificationResponse retryNotification(Long id);

    // =============================================================================
    // Batch Operations
    // =============================================================================

    /**
     * Send notifications to multiple recipients.
     * 
     * @param requests List of notification requests
     * @return List of notification responses
     */
    List<NotificationResponse> sendBatchNotifications(List<SendNotificationRequest> requests);

    /**
     * Get pending notifications ready to be sent.
     * 
     * @return List of notification responses
     */
    List<NotificationResponse> getPendingNotifications();

    /**
     * Get failed notifications that can be retried.
     * 
     * @return List of notification responses
     */
    List<NotificationResponse> getFailedNotificationsForRetry();

    // =============================================================================
    // Analytics and Reporting
    // =============================================================================

    /**
     * Get notification statistics for a user.
     * 
     * @param userId User ID
     * @return Notification statistics
     */
    Object getUserNotificationStats(Long userId);

    /**
     * Get notification statistics for a time period.
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return Notification statistics
     */
    Object getNotificationStats(String startDate, String endDate);

    /**
     * Get delivery statistics by channel.
     * 
     * @param channel Notification channel
     * @return Delivery statistics
     */
    Object getDeliveryStatsByChannel(String channel);

    // =============================================================================
    // Template Management
    // =============================================================================

    /**
     * Get available notification templates.
     * 
     * @return List of template information
     */
    List<Object> getAvailableTemplates();

    /**
     * Get template by ID.
     * 
     * @param templateId Template ID
     * @return Template information
     */
    Object getTemplateById(String templateId);

    /**
     * Create new notification template.
     * 
     * @param template Template data
     * @return Created template information
     */
    Object createTemplate(Object template);

    /**
     * Update notification template.
     * 
     * @param templateId Template ID
     * @param template Updated template data
     * @return Updated template information
     */
    Object updateTemplate(String templateId, Object template);

    /**
     * Delete notification template.
     * 
     * @param templateId Template ID
     */
    void deleteTemplate(String templateId);

    // =============================================================================
    // User Preferences
    // =============================================================================

    /**
     * Get user notification preferences.
     * 
     * @param userId User ID
     * @return User preferences
     */
    Object getUserPreferences(Long userId);

    /**
     * Update user notification preferences.
     * 
     * @param userId User ID
     * @param preferences Updated preferences
     * @return Updated preferences
     */
    Object updateUserPreferences(Long userId, Object preferences);

    /**
     * Enable notification channel for user.
     * 
     * @param userId User ID
     * @param channel Notification channel
     */
    void enableNotificationChannel(Long userId, String channel);

    /**
     * Disable notification channel for user.
     * 
     * @param userId User ID
     * @param channel Notification channel
     */
    void disableNotificationChannel(Long userId, String channel);

    // =============================================================================
    // Health and Monitoring
    // =============================================================================

    /**
     * Check notification service health.
     * 
     * @return Health status
     */
    Object getServiceHealth();

    /**
     * Get notification service metrics.
     * 
     * @return Service metrics
     */
    Object getServiceMetrics();

    /**
     * Get notification queue status.
     * 
     * @return Queue status information
     */
    Object getQueueStatus();

    // =============================================================================
    // Administrative Operations
    // =============================================================================

    /**
     * Purge old notifications.
     * 
     * @param daysOld Number of days old
     * @return Number of notifications purged
     */
    int purgeOldNotifications(int daysOld);

    /**
     * Resend failed notifications.
     * 
     * @return Number of notifications resent
     */
    int resendFailedNotifications();

    /**
     * Update notification service configuration.
     * 
     * @param config Configuration data
     * @return Updated configuration
     */
    Object updateServiceConfiguration(Object config);

    /**
     * Get notification service configuration.
     * 
     * @return Current configuration
     */
    Object getServiceConfiguration();
}
