package com.legacykeep.notification.service;

import com.legacykeep.notification.entity.Notification;
import com.legacykeep.notification.entity.NotificationChannel;
import com.legacykeep.notification.entity.NotificationDelivery;
import com.legacykeep.notification.entity.NotificationStatus;
import com.legacykeep.notification.repository.NotificationDeliveryRepository;
import com.legacykeep.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Notification Delivery Service for handling notification delivery.
 * 
 * Provides business logic for sending notifications across different
 * channels (email, push, SMS, in-app) with retry logic and error handling.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final EmailDeliveryService emailDeliveryService;
    private final PushDeliveryService pushDeliveryService;
    private final SmsDeliveryService smsDeliveryService;
    private final InAppDeliveryService inAppDeliveryService;

    // =============================================================================
    // Async Processing
    // =============================================================================

    /**
     * Process notification asynchronously.
     * 
     * This method is called asynchronously to handle the actual
     * delivery of notifications across different channels.
     */
    @Transactional
    public CompletableFuture<Void> processNotificationAsync(Notification notification) {
        log.info("Processing notification synchronously: id={}, type={}", 
                notification.getId(), notification.getNotificationType());

        try {
            // Mark notification as processing
            notification.markAsProcessing();
            notificationRepository.save(notification);

            // Create delivery record
            NotificationDelivery delivery = createDeliveryRecord(notification);

            // Process based on channel
            boolean success = processNotificationByChannel(notification, delivery);

            if (success) {
                // Mark as sent
                notification.markAsSent();
                delivery.markAsSent();
                
                log.info("Email sent successfully: id={}, to={}", 
                        notification.getId(), notification.getRecipientEmail());
            } else {
                // Mark as failed
                notification.markAsFailed("Delivery service unavailable");
                delivery.markAsFailed("Delivery service unavailable");
            }

            // Save updated records
            notificationRepository.save(notification);
            deliveryRepository.save(delivery);

            log.info("Notification processing completed: id={}, success={}", 
                    notification.getId(), success);

        } catch (Exception e) {
            log.error("Error processing notification: id={}, error={}", 
                    notification.getId(), e.getMessage(), e);
            
            handleProcessingError(notification, e);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Process notification by channel.
     */
    private boolean processNotificationByChannel(Notification notification, NotificationDelivery delivery) {
        try {
            switch (notification.getNotificationType()) {
                case EMAIL:
                    return emailDeliveryService.sendEmail(notification, delivery);
                case PUSH:
                    return pushDeliveryService.sendPushNotification(notification, delivery);
                case SMS:
                    return smsDeliveryService.sendSms(notification, delivery);
                case IN_APP:
                    return inAppDeliveryService.sendInAppNotification(notification, delivery);
                default:
                    log.error("Unsupported notification type: {}", notification.getNotificationType());
                    return false;
            }
        } catch (Exception e) {
            log.error("Error sending notification via {}: {}", 
                    notification.getNotificationType(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Confirm delivery asynchronously.
     */
    @Async("notificationTaskExecutor")
    @Transactional
    public void confirmDelivery(Notification notification, NotificationDelivery delivery) {
        log.debug("Confirming delivery for notification: id={}", notification.getId());

        try {
            // Simulate delivery confirmation delay
            Thread.sleep(1000);

            // Mark as delivered
            notification.markAsDelivered();
            delivery.markAsDelivered();

            // Save updated records
            notificationRepository.save(notification);
            deliveryRepository.save(delivery);

            log.info("Delivery confirmed for notification: id={}", notification.getId());

        } catch (Exception e) {
            log.error("Error confirming delivery for notification: id={}, error={}", 
                    notification.getId(), e.getMessage(), e);
        }
    }

    // =============================================================================
    // Retry Logic
    // =============================================================================

    /**
     * Retry failed notification.
     */
    @Async("notificationTaskExecutor")
    @Transactional
    public CompletableFuture<Void> retryNotificationAsync(Notification notification) {
        log.info("Retrying failed notification: id={}, retryCount={}", 
                notification.getId(), notification.getRetryCount());

        try {
            // Mark as processing
            notification.markAsProcessing();
            notificationRepository.save(notification);

            // Create new delivery record for retry
            NotificationDelivery delivery = createDeliveryRecord(notification);

            // Process retry
            boolean success = processNotificationByChannel(notification, delivery);

            if (success) {
                notification.markAsSent();
                delivery.markAsSent();
                
                // Attempt delivery confirmation (async)
                CompletableFuture.runAsync(() -> confirmDelivery(notification, delivery));
            } else {
                notification.markAsFailed("Retry failed - delivery service unavailable");
                delivery.markAsFailed("Retry failed - delivery service unavailable");
            }

            // Save updated records
            notificationRepository.save(notification);
            deliveryRepository.save(delivery);

            log.info("Notification retry completed: id={}, success={}", 
                    notification.getId(), success);

        } catch (Exception e) {
            log.error("Error retrying notification: id={}, error={}", 
                    notification.getId(), e.getMessage(), e);
            
            handleProcessingError(notification, e);
        }

        return CompletableFuture.completedFuture(null);
    }

    // =============================================================================
    // Batch Processing
    // =============================================================================

    /**
     * Process batch of pending notifications.
     */
    @Async("notificationTaskExecutor")
    @Transactional
    public CompletableFuture<Void> processPendingNotificationsAsync() {
        log.info("Processing pending notifications batch");

        try {
            // Get pending notifications ready to be sent
            var pendingNotifications = notificationRepository.findPendingNotificationsReadyToSend(LocalDateTime.now());

            for (Notification notification : pendingNotifications) {
                processNotificationAsync(notification);
            }

            log.info("Batch processing completed: {} notifications", pendingNotifications.size());

        } catch (Exception e) {
            log.error("Error processing pending notifications batch: {}", e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Retry failed notifications batch.
     */
    @Async("notificationTaskExecutor")
    @Transactional
    public CompletableFuture<Void> retryFailedNotificationsAsync() {
        log.info("Retrying failed notifications batch");

        try {
            // Get failed notifications that can be retried
            var failedNotifications = notificationRepository.findFailedNotificationsForRetry();

            for (Notification notification : failedNotifications) {
                retryNotificationAsync(notification);
            }

            log.info("Batch retry completed: {} notifications", failedNotifications.size());

        } catch (Exception e) {
            log.error("Error retrying failed notifications batch: {}", e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    // =============================================================================
    // Delivery Management
    // =============================================================================

    /**
     * Get delivery by notification ID.
     */
    @Transactional(readOnly = true)
    public NotificationDelivery getDeliveryByNotificationId(Long notificationId) {
        log.debug("Fetching delivery for notification: {}", notificationId);

        return deliveryRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found for notification: " + notificationId));
    }

    /**
     * Get deliveries by status.
     */
    @Transactional(readOnly = true)
    public java.util.List<NotificationDelivery> getDeliveriesByStatus(NotificationStatus status) {
        log.debug("Fetching deliveries by status: {}", status);

        return deliveryRepository.findByStatus(status);
    }

    /**
     * Get deliveries by channel.
     */
    @Transactional(readOnly = true)
    public java.util.List<NotificationDelivery> getDeliveriesByChannel(NotificationChannel channel) {
        log.debug("Fetching deliveries by channel: {}", channel);

        return deliveryRepository.findByChannel(channel);
    }

    // =============================================================================
    // Helper Methods
    // =============================================================================

    /**
     * Create delivery record.
     */
    private NotificationDelivery createDeliveryRecord(Notification notification) {
        NotificationDelivery delivery = new NotificationDelivery(
                notification.getId(),
                NotificationChannel.valueOf(notification.getNotificationType().name())
        );

        return deliveryRepository.save(delivery);
    }

    /**
     * Handle processing error.
     */
    private void handleProcessingError(Notification notification, Exception e) {
        try {
            notification.markAsFailed("Processing error: " + e.getMessage());
            notificationRepository.save(notification);

            // Create delivery record for failed attempt
            NotificationDelivery delivery = createDeliveryRecord(notification);
            delivery.markAsFailed("Processing error: " + e.getMessage());
            deliveryRepository.save(delivery);

        } catch (Exception saveError) {
            log.error("Error saving failed notification state: {}", saveError.getMessage(), saveError);
        }
    }

    // =============================================================================
    // Health Check Methods
    // =============================================================================

    /**
     * Check delivery service health.
     */
    public boolean isHealthy() {
        try {
            // Check if all delivery services are available
            boolean emailHealthy = emailDeliveryService.isHealthy();
            boolean pushHealthy = pushDeliveryService.isHealthy();
            boolean smsHealthy = smsDeliveryService.isHealthy();
            boolean inAppHealthy = inAppDeliveryService.isHealthy();

            return emailHealthy && pushHealthy && smsHealthy && inAppHealthy;

        } catch (Exception e) {
            log.error("Error checking delivery service health: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get delivery service status.
     */
    public java.util.Map<String, Boolean> getServiceStatus() {
        return java.util.Map.of(
                "email", emailDeliveryService.isHealthy(),
                "push", pushDeliveryService.isHealthy(),
                "sms", smsDeliveryService.isHealthy(),
                "inApp", inAppDeliveryService.isHealthy()
        );
    }
}
