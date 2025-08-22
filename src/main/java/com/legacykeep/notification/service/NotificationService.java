package com.legacykeep.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacykeep.notification.dto.request.SendNotificationRequest;
import com.legacykeep.notification.dto.response.NotificationResponse;
import com.legacykeep.notification.entity.*;
import com.legacykeep.notification.repository.NotificationEventRepository;
import com.legacykeep.notification.repository.NotificationRepository;
import com.legacykeep.notification.repository.NotificationTemplateRepository;
import com.legacykeep.notification.repository.UserNotificationPreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Notification Service for managing notification lifecycle.
 * 
 * Provides business logic for notification creation, processing,
 * delivery tracking, and management with comprehensive validation
 * and error handling.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

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

    /**
     * Create and send a notification.
     * 
     * This method handles the complete notification process:
     * - Validates request data
     * - Checks user preferences
     * - Creates notification record
     * - Processes template
     * - Initiates delivery
     * - Logs events
     */
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

    /**
     * Get notification by ID.
     */
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        log.debug("Fetching notification by ID: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + id));

        return buildNotificationResponse(notification);
    }

    /**
     * Get notification by event ID.
     */
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationByEventId(String eventId) {
        log.debug("Fetching notification by event ID: {}", eventId);

        Notification notification = notificationRepository.findByEventId(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with event ID: " + eventId));

        return buildNotificationResponse(notification);
    }

    /**
     * Get notifications for a user with pagination.
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        log.debug("Fetching notifications for user: {}, page: {}, size: {}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());

        Page<Notification> notifications = notificationRepository.findByRecipientId(userId, pageable);

        return notifications.map(this::buildNotificationResponse);
    }

    /**
     * Get notifications by status.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByStatus(NotificationStatus status) {
        log.debug("Fetching notifications by status: {}", status);

        List<Notification> notifications = notificationRepository.findByStatus(status);

        return notifications.stream()
                .map(this::buildNotificationResponse)
                .toList();
    }

    /**
     * Cancel a notification.
     */
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

    /**
     * Retry a failed notification.
     */
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

    /**
     * Send notifications to multiple recipients.
     */
    @Transactional
    public List<NotificationResponse> sendBatchNotifications(List<SendNotificationRequest> requests) {
        log.info("Processing batch notification request: {} notifications", requests.size());

        return requests.stream()
                .map(this::sendNotification)
                .toList();
    }

    /**
     * Get pending notifications ready to be sent.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getPendingNotifications() {
        log.debug("Fetching pending notifications ready to be sent");

        List<Notification> notifications = notificationRepository.findPendingNotificationsReadyToSend(LocalDateTime.now());

        return notifications.stream()
                .map(this::buildNotificationResponse)
                .toList();
    }

    /**
     * Get failed notifications that can be retried.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getFailedNotificationsForRetry() {
        log.debug("Fetching failed notifications that can be retried");

        List<Notification> notifications = notificationRepository.findFailedNotificationsForRetry();

        return notifications.stream()
                .map(this::buildNotificationResponse)
                .toList();
    }

    // =============================================================================
    // Validation Methods
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
        if (request.getPriority() != NotificationPriority.URGENT && preferences.isInQuietHours()) {
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

    // =============================================================================
    // Helper Methods
    // =============================================================================

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

        notification.setChannel(request.getChannel());
        notification.setPriority(request.getPriority());
        notification.setScheduledAt(request.getScheduledAt());
        notification.setMaxRetries(request.getMaxRetries());
        notification.setCorrelationId(request.getCorrelationId());
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
                .channel(notification.getChannel())
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
                .correlationId(notification.getCorrelationId())
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
                    notification.getCorrelationId(),
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
