package com.legacykeep.notification.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacykeep.notification.dto.request.SendNotificationRequest;
import com.legacykeep.notification.dto.response.NotificationResponse;
import com.legacykeep.notification.entity.*;
import com.legacykeep.notification.repository.NotificationEventRepository;
import com.legacykeep.notification.repository.NotificationRepository;
import com.legacykeep.notification.repository.NotificationTemplateRepository;
import com.legacykeep.notification.repository.UserNotificationPreferencesRepository;
import com.legacykeep.notification.service.NotificationDeliveryService;
import com.legacykeep.notification.service.NotificationService;
import com.legacykeep.notification.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Notification Service Implementation
 * 
 * Provides the concrete implementation of notification operations
 * including notification creation, management, and delivery tracking.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final UserNotificationPreferencesRepository preferencesRepository;
    private final NotificationEventRepository eventRepository;
    private final NotificationTemplateService templateService;
    private final NotificationDeliveryService deliveryService;
    private final ObjectMapper objectMapper;

    // =============================================================================
    // Notification Creation and Management
    // =============================================================================

    @Override
    @Transactional
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        log.info("Processing notification request: templateId={}, recipientId={}, channel={}", 
                request.getTemplateId(), request.getRecipientId(), request.getChannel());

        // Validate request
        validateNotificationRequest(request);

        // Check user preferences
        checkUserPreferences(request);

        // Get template
        NotificationTemplate template = getTemplate(request.getTemplateId());

        // Create notification
        Notification notification = createNotification(request, template);

        // Process template and set content
        processNotificationContent(notification, template, request);

        // Save notification
        Notification savedNotification = notificationRepository.save(notification);

        // Log event
        logNotificationEvent(savedNotification, "NOTIFICATION_CREATED", request);

        // Initiate delivery (async)
        deliveryService.processNotificationAsync(savedNotification);

        log.info("Notification created successfully: id={}, eventId={}", 
                savedNotification.getId(), savedNotification.getEventId());

        return buildNotificationResponse(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        log.debug("Fetching notification by ID: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + id));

        return buildNotificationResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationByEventId(String eventId) {
        log.debug("Fetching notification by event ID: {}", eventId);

        Notification notification = notificationRepository.findByEventId(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with event ID: " + eventId));

        return buildNotificationResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        log.debug("Fetching notifications for user: {}, page: {}, size: {}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());

        Page<Notification> notifications = notificationRepository.findByRecipientId(userId, pageable);

        return notifications.map(this::buildNotificationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByStatus(NotificationStatus status) {
        log.debug("Fetching notifications by status: {}", status);

        List<Notification> notifications = notificationRepository.findByStatus(status);

        return notifications.stream()
                .map(this::buildNotificationResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse cancelNotification(Long id) {
        log.info("Cancelling notification: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + id));

        if (notification.getStatus() == NotificationStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel already delivered notification");
        }

        notification.cancel();
        Notification savedNotification = notificationRepository.save(notification);

        logNotificationEvent(savedNotification, "NOTIFICATION_CANCELLED", null);

        return buildNotificationResponse(savedNotification);
    }

    @Override
    @Transactional
    public NotificationResponse retryNotification(Long id) {
        log.info("Retrying notification: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + id));

        if (!notification.canRetry()) {
            throw new IllegalStateException("Notification cannot be retried");
        }

        notification.markAsProcessing();
        Notification savedNotification = notificationRepository.save(notification);

        // Initiate retry delivery (async)
        deliveryService.processNotificationAsync(savedNotification);

        logNotificationEvent(savedNotification, "NOTIFICATION_RETRY", null);

        return buildNotificationResponse(savedNotification);
    }

    // =============================================================================
    // Batch Operations
    // =============================================================================

    @Override
    @Transactional
    public List<NotificationResponse> sendBatchNotifications(List<SendNotificationRequest> requests) {
        log.info("Processing batch notification request: {} notifications", requests.size());

        return requests.stream()
                .map(this::sendNotification)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getPendingNotifications() {
        log.debug("Fetching pending notifications ready to be sent");

        List<Notification> notifications = notificationRepository.findPendingNotificationsReadyToSend(LocalDateTime.now());

        return notifications.stream()
                .map(this::buildNotificationResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getFailedNotificationsForRetry() {
        log.debug("Fetching failed notifications that can be retried");

        List<Notification> notifications = notificationRepository.findFailedNotificationsForRetry();

        return notifications.stream()
                .map(this::buildNotificationResponse)
                .toList();
    }

    // =============================================================================
    // Analytics and Reporting
    // =============================================================================

    @Override
    @Transactional(readOnly = true)
    public Object getUserNotificationStats(Long userId) {
        log.debug("Getting notification stats for user: {}", userId);
        // TODO: Implement user notification statistics
        return Map.of("userId", userId, "totalNotifications", 0, "delivered", 0, "failed", 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getNotificationStats(String startDate, String endDate) {
        log.debug("Getting notification stats from {} to {}", startDate, endDate);
        // TODO: Implement notification statistics for time period
        return Map.of("startDate", startDate, "endDate", endDate, "totalNotifications", 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getDeliveryStatsByChannel(String channel) {
        log.debug("Getting delivery stats for channel: {}", channel);
        // TODO: Implement delivery statistics by channel
        return Map.of("channel", channel, "totalSent", 0, "delivered", 0, "failed", 0);
    }

    // =============================================================================
    // Template Management
    // =============================================================================

    @Override
    @Transactional(readOnly = true)
    public List<Object> getAvailableTemplates() {
        log.debug("Getting available notification templates");
        // TODO: Implement template listing
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getTemplateById(String templateId) {
        log.debug("Getting template by ID: {}", templateId);
        // TODO: Implement template retrieval
        return Map.of("templateId", templateId, "name", "Template", "active", true);
    }

    @Override
    @Transactional
    public Object createTemplate(Object template) {
        log.info("Creating new notification template");
        // TODO: Implement template creation
        return Map.of("templateId", UUID.randomUUID().toString(), "status", "created");
    }

    @Override
    @Transactional
    public Object updateTemplate(String templateId, Object template) {
        log.info("Updating template: {}", templateId);
        // TODO: Implement template update
        return Map.of("templateId", templateId, "status", "updated");
    }

    @Override
    @Transactional
    public void deleteTemplate(String templateId) {
        log.info("Deleting template: {}", templateId);
        // TODO: Implement template deletion
    }

    // =============================================================================
    // User Preferences
    // =============================================================================

    @Override
    @Transactional(readOnly = true)
    public Object getUserPreferences(Long userId) {
        log.debug("Getting user preferences for user: {}", userId);
        // TODO: Implement user preferences retrieval
        return Map.of("userId", userId, "emailEnabled", true, "pushEnabled", true, "smsEnabled", false);
    }

    @Override
    @Transactional
    public Object updateUserPreferences(Long userId, Object preferences) {
        log.info("Updating user preferences for user: {}", userId);
        // TODO: Implement user preferences update
        return Map.of("userId", userId, "status", "updated");
    }

    @Override
    @Transactional
    public void enableNotificationChannel(Long userId, String channel) {
        log.info("Enabling {} notifications for user: {}", channel, userId);
        // TODO: Implement channel enablement
    }

    @Override
    @Transactional
    public void disableNotificationChannel(Long userId, String channel) {
        log.info("Disabling {} notifications for user: {}", channel, userId);
        // TODO: Implement channel disablement
    }

    // =============================================================================
    // Health and Monitoring
    // =============================================================================

    @Override
    public Object getServiceHealth() {
        log.debug("Getting notification service health");
        return Map.of(
                "status", "HEALTHY",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0",
                "emailService", deliveryService.isHealthy()
        );
    }

    @Override
    public Object getServiceMetrics() {
        log.debug("Getting notification service metrics");
        return Map.of(
                "totalNotifications", 0,
                "pendingNotifications", 0,
                "failedNotifications", 0,
                "deliveredNotifications", 0
        );
    }

    @Override
    public Object getQueueStatus() {
        log.debug("Getting notification queue status");
        return Map.of(
                "queueSize", 0,
                "processingRate", 0,
                "errorRate", 0
        );
    }

    // =============================================================================
    // Administrative Operations
    // =============================================================================

    @Override
    @Transactional
    public int purgeOldNotifications(int daysOld) {
        log.info("Purging notifications older than {} days", daysOld);
        // TODO: Implement notification purging
        return 0;
    }

    @Override
    @Transactional
    public int resendFailedNotifications() {
        log.info("Resending failed notifications");
        // TODO: Implement failed notification resending
        return 0;
    }

    @Override
    public Object updateServiceConfiguration(Object config) {
        log.info("Updating notification service configuration");
        // TODO: Implement configuration update
        return Map.of("status", "updated", "timestamp", LocalDateTime.now());
    }

    @Override
    public Object getServiceConfiguration() {
        log.debug("Getting notification service configuration");
        return Map.of(
                "emailEnabled", true,
                "pushEnabled", true,
                "smsEnabled", false,
                "maxRetries", 3,
                "rateLimitEnabled", true
        );
    }

    // =============================================================================
    // Private Helper Methods
    // =============================================================================

    /**
     * Validate notification request.
     */
    private void validateNotificationRequest(SendNotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Notification request cannot be null");
        }

        if (!request.isValidForChannel()) {
            throw new IllegalArgumentException("Invalid recipient information for channel: " + request.getChannel());
        }

        if (request.getScheduledAt() != null && request.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Scheduled time cannot be in the past");
        }

        if (request.getMaxRetries() != null && (request.getMaxRetries() < 0 || request.getMaxRetries() > 10)) {
            throw new IllegalArgumentException("Max retries must be between 0 and 10");
        }
    }

    /**
     * Check user notification preferences.
     */
    private void checkUserPreferences(SendNotificationRequest request) {
        UserNotificationPreferences preferences = preferencesRepository.findByUserIdOrCreateDefault(request.getRecipientId());

        if (!preferences.isChannelEnabled(request.getChannel())) {
            throw new IllegalStateException("User has disabled " + request.getChannel() + " notifications");
        }

        // Check quiet hours for non-urgent notifications
        if (request.getPriority() != NotificationPriority.URGENT && preferences.isWithinQuietHours(LocalTime.now())) {
            log.info("Notification scheduled during quiet hours for user: {}", request.getRecipientId());
            // Could implement quiet hours logic here
        }
    }

    /**
     * Get notification template.
     */
    private NotificationTemplate getTemplate(String templateId) {
        return templateRepository.findByTemplateIdAndIsActiveTrue(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Active template not found: " + templateId));
    }

    /**
     * Create notification entity from request.
     */
    private Notification createNotification(SendNotificationRequest request, NotificationTemplate template) {
        String eventId = generateEventId();

        Notification notification = new Notification(
                eventId,
                request.getNotificationType(),
                request.getTemplateId(),
                request.getRecipientId()
        );

        notification.setPriority(request.getPriority());
        notification.setScheduledAt(request.getScheduledAt());
        notification.setMaxRetries(request.getMaxRetries());
        notification.setCreatedBy(request.getSourceService());

        // Set recipient information based on channel
        switch (request.getChannel()) {
            case EMAIL:
                notification.setRecipientEmail(request.getRecipientEmail());
                break;
            case SMS:
                notification.setRecipientPhone(request.getRecipientPhone());
                break;
            case PUSH:
                notification.setRecipientDeviceToken(request.getRecipientDeviceToken());
                break;
        }

        return notification;
    }

    /**
     * Process notification content using template.
     */
    private void processNotificationContent(Notification notification, NotificationTemplate template, SendNotificationRequest request) {
        try {
            // Process subject
            if (request.getSubject() != null) {
                notification.setSubject(request.getSubject());
            } else if (template.hasSubject()) {
                String processedSubject = templateService.processTemplate(template.getSubjectTemplate(), request.getTemplateVariables());
                notification.setSubject(processedSubject);
            }

            // Process content
            if (request.getCustomContent() != null) {
                notification.setContent(request.getCustomContent());
            } else {
                String processedContent = templateService.processTemplate(template.getTemplateContent(), request.getTemplateVariables());
                notification.setContent(processedContent);
            }

            // Store template variables as JSON
            if (request.getTemplateVariables() != null) {
                notification.setTemplateData(objectMapper.writeValueAsString(request.getTemplateVariables()));
            }

            // Store metadata as JSON
            if (request.getMetadata() != null) {
                notification.setMetadata(objectMapper.writeValueAsString(request.getMetadata()));
            }

        } catch (JsonProcessingException e) {
            log.error("Error processing notification content: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing notification content", e);
        }
    }

    /**
     * Build notification response DTO.
     */
    private NotificationResponse buildNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .eventId(notification.getEventId())
                .templateId(notification.getTemplateId())
                .notificationType(notification.getNotificationType())
                .channel(NotificationChannel.valueOf(notification.getNotificationType().name()))
                .priority(notification.getPriority())
                .status(notification.getStatus())
                .recipientId(notification.getRecipientId())
                .recipientEmail(maskEmail(notification.getRecipientEmail()))
                .recipientPhone(maskPhone(notification.getRecipientPhone()))
                .subject(notification.getSubject())
                .createdAt(notification.getCreatedAt())
                .scheduledAt(notification.getScheduledAt())
                .sentAt(notification.getSentAt())
                .deliveredAt(notification.getDeliveredAt())
                .failedAt(notification.getFailedAt())
                .retryCount(notification.getRetryCount())
                .maxRetries(notification.getMaxRetries())
                .failureReason(notification.getFailureReason())
                .metadata(notification.getMetadata())
                .build();
    }

    /**
     * Log notification event.
     */
    private void logNotificationEvent(Notification notification, String eventType, SendNotificationRequest request) {
        try {
            NotificationEvent event = new NotificationEvent(
                    notification.getEventId(),
                    eventType,
                    "NOTIFICATION_SERVICE",
                    notification.getRecipientId(),
                    null, // correlationId not available in Notification entity
                    request != null ? request.getRequestId() : null,
                    objectMapper.writeValueAsString(notification)
            );

            eventRepository.save(event);
        } catch (JsonProcessingException e) {
            log.error("Error logging notification event: {}", e.getMessage(), e);
        }
    }

    /**
     * Generate unique event ID.
     */
    private String generateEventId() {
        return "notif-" + UUID.randomUUID().toString();
    }

    /**
     * Mask email for privacy.
     */
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        return email.charAt(0) + "***@" + email.substring(atIndex + 1);
    }

    /**
     * Mask phone number for privacy.
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return null;
        }
        if (phone.length() <= 4) {
            return phone;
        }
        return "***" + phone.substring(phone.length() - 4);
    }
}
