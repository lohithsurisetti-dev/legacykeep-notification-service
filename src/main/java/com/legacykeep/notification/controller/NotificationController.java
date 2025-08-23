package com.legacykeep.notification.controller;

import com.legacykeep.notification.dto.ApiResponse;
import com.legacykeep.notification.dto.request.SendNotificationRequest;
import com.legacykeep.notification.dto.response.NotificationResponse;
import com.legacykeep.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Notification Controller
 * 
 * Provides REST API endpoints for notification management including
 * sending notifications, retrieving notification status, and managing
 * notification lifecycle.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // =============================================================================
    // Notification Creation and Sending
    // =============================================================================

    /**
     * Send a notification.
     * 
     * @param request Notification request data
     * @return Notification response with details
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {
        
        log.info("Received notification request: templateId={}, recipientId={}, channel={}", 
                request.getTemplateId(), request.getRecipientId(), request.getChannel());

        try {
            NotificationResponse response = notificationService.sendNotification(request);
            
            log.info("Notification sent successfully: id={}, eventId={}", 
                    response.getId(), response.getEventId());

            return ResponseEntity.ok(ApiResponse.success(response, "Notification sent successfully"));
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid notification request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
                    
        } catch (IllegalStateException e) {
            log.warn("Notification request rejected: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage(), "REQUEST_REJECTED"));
                    
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to send notification", "INTERNAL_ERROR"));
        }
    }

    /**
     * Send notifications to multiple recipients in batch.
     * 
     * @param requests List of notification requests
     * @return List of notification responses
     */
    @PostMapping("/send/batch")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> sendBatchNotifications(
            @Valid @RequestBody List<SendNotificationRequest> requests) {
        
        log.info("Received batch notification request: {} notifications", requests.size());

        try {
            List<NotificationResponse> responses = notificationService.sendBatchNotifications(requests);
            
            log.info("Batch notifications sent successfully: {} processed", responses.size());

            return ResponseEntity.ok(ApiResponse.success(responses, 
                    String.format("Successfully sent %d notifications", responses.size())));
                    
        } catch (Exception e) {
            log.error("Error sending batch notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to send batch notifications", "INTERNAL_ERROR"));
        }
    }

    // =============================================================================
    // Notification Retrieval
    // =============================================================================

    /**
     * Get notification by ID.
     * 
     * @param id Notification ID
     * @return Notification response
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotification(@PathVariable Long id) {
        
        log.debug("Fetching notification by ID: {}", id);

        try {
            NotificationResponse response = notificationService.getNotificationById(id);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Notification retrieved successfully"));
            
        } catch (IllegalArgumentException e) {
            log.warn("Notification not found: id={}, error={}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Notification not found", "NOT_FOUND"));
                    
        } catch (Exception e) {
            log.error("Error fetching notification: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve notification", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get notification by event ID.
     * 
     * @param eventId Event ID
     * @return Notification response
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationByEventId(
            @PathVariable String eventId) {
        
        log.debug("Fetching notification by event ID: {}", eventId);

        try {
            NotificationResponse response = notificationService.getNotificationByEventId(eventId);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Notification retrieved successfully"));
            
        } catch (IllegalArgumentException e) {
            log.warn("Notification not found: eventId={}, error={}", eventId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Notification not found", "NOT_FOUND"));
                    
        } catch (Exception e) {
            log.error("Error fetching notification: eventId={}, error={}", eventId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve notification", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get notifications for a user with pagination.
     * 
     * @param userId User ID
     * @param page Page number (default: 0)
     * @param size Page size (default: 20, max: 100)
     * @param sort Sort field (default: createdAt)
     * @param direction Sort direction (default: DESC)
     * @return Page of notification responses
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        log.debug("Fetching notifications for user: userId={}, page={}, size={}, sort={}, direction={}", 
                userId, page, size, sort, direction);

        try {
            // Validate pagination parameters
            if (page < 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Page number must be non-negative", "INVALID_PAGE"));
            }
            
            if (size <= 0 || size > 100) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Page size must be between 1 and 100", "INVALID_SIZE"));
            }

            // Create pageable object
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

            Page<NotificationResponse> response = notificationService.getUserNotifications(userId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(response, "User notifications retrieved successfully"));
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid pagination parameters: userId={}, error={}", userId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "INVALID_PARAMETERS"));
                    
        } catch (Exception e) {
            log.error("Error fetching user notifications: userId={}, error={}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user notifications", "INTERNAL_ERROR"));
        }
    }

    // =============================================================================
    // Notification Management
    // =============================================================================

    /**
     * Cancel a notification.
     * 
     * @param id Notification ID
     * @return Notification response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> cancelNotification(@PathVariable Long id) {
        
        log.info("Cancelling notification: {}", id);

        try {
            NotificationResponse response = notificationService.cancelNotification(id);
            
            log.info("Notification cancelled successfully: id={}", id);

            return ResponseEntity.ok(ApiResponse.success(response, "Notification cancelled successfully"));
            
        } catch (IllegalArgumentException e) {
            log.warn("Notification not found for cancellation: id={}, error={}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Notification not found", "NOT_FOUND"));
                    
        } catch (IllegalStateException e) {
            log.warn("Cannot cancel notification: id={}, error={}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage(), "CANCELLATION_FAILED"));
                    
        } catch (Exception e) {
            log.error("Error cancelling notification: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to cancel notification", "INTERNAL_ERROR"));
        }
    }

    /**
     * Retry a failed notification.
     * 
     * @param id Notification ID
     * @return Notification response
     */
    @PostMapping("/{id}/retry")
    public ResponseEntity<ApiResponse<NotificationResponse>> retryNotification(@PathVariable Long id) {
        
        log.info("Retrying notification: {}", id);

        try {
            NotificationResponse response = notificationService.retryNotification(id);
            
            log.info("Notification retry initiated successfully: id={}", id);

            return ResponseEntity.ok(ApiResponse.success(response, "Notification retry initiated successfully"));
            
        } catch (IllegalArgumentException e) {
            log.warn("Notification not found for retry: id={}, error={}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Notification not found", "NOT_FOUND"));
                    
        } catch (IllegalStateException e) {
            log.warn("Cannot retry notification: id={}, error={}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage(), "RETRY_FAILED"));
                    
        } catch (Exception e) {
            log.error("Error retrying notification: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retry notification", "INTERNAL_ERROR"));
        }
    }

    // =============================================================================
    // Administrative Endpoints
    // =============================================================================

    /**
     * Get pending notifications ready to be sent.
     * 
     * @return List of notification responses
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getPendingNotifications() {
        
        log.debug("Fetching pending notifications");

        try {
            List<NotificationResponse> response = notificationService.getPendingNotifications();
            
            return ResponseEntity.ok(ApiResponse.success(response, 
                    String.format("Found %d pending notifications", response.size())));
                    
        } catch (Exception e) {
            log.error("Error fetching pending notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve pending notifications", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get failed notifications that can be retried.
     * 
     * @return List of notification responses
     */
    @GetMapping("/failed/retryable")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getFailedNotificationsForRetry() {
        
        log.debug("Fetching failed notifications for retry");

        try {
            List<NotificationResponse> response = notificationService.getFailedNotificationsForRetry();
            
            return ResponseEntity.ok(ApiResponse.success(response, 
                    String.format("Found %d failed notifications available for retry", response.size())));
                    
        } catch (Exception e) {
            log.error("Error fetching failed notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve failed notifications", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get notification service health status.
     * 
     * @return Service health information
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Object>> getServiceHealth() {
        
        log.debug("Checking notification service health");

        try {
            Object health = notificationService.getServiceHealth();
            
            return ResponseEntity.ok(ApiResponse.success(health, "Service health retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error checking service health: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve service health", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get notification service metrics.
     * 
     * @return Service metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Object>> getServiceMetrics() {
        
        log.debug("Fetching notification service metrics");

        try {
            Object metrics = notificationService.getServiceMetrics();
            
            return ResponseEntity.ok(ApiResponse.success(metrics, "Service metrics retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error fetching service metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve service metrics", "INTERNAL_ERROR"));
        }
    }
}
