package com.legacykeep.notification.exception;

import com.legacykeep.notification.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for Notification Service
 * 
 * Provides consistent error handling and response formatting
 * for all exceptions thrown by the notification service.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =============================================================================
    // Notification Service Exceptions
    // =============================================================================

    /**
     * Handle NotificationException.
     */
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotificationException(
            NotificationException ex, WebRequest request) {
        
        log.warn("Notification exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    /**
     * Handle TemplateNotFoundException.
     */
    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleTemplateNotFoundException(
            TemplateNotFoundException ex, WebRequest request) {
        
        log.warn("Template not found: {}", ex.getTemplateId());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    /**
     * Handle InvalidNotificationRequestException.
     */
    @ExceptionHandler(InvalidNotificationRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidNotificationRequestException(
            InvalidNotificationRequestException ex, WebRequest request) {
        
        log.warn("Invalid notification request: {} - {}", ex.getField(), ex.getReason());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    /**
     * Handle DeliveryFailedException.
     */
    @ExceptionHandler(DeliveryFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleDeliveryFailedException(
            DeliveryFailedException ex, WebRequest request) {
        
        log.error("Delivery failed: {} to {} via {} - {}", 
                ex.getRecipient(), ex.getChannel(), ex.getFailureReason());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    // =============================================================================
    // Validation Exceptions
    // =============================================================================

    /**
     * Handle validation errors from @Valid annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Validation failed", "VALIDATION_ERROR", errors.toString()));
    }

    // =============================================================================
    // Standard Java Exceptions
    // =============================================================================

    /**
     * Handle IllegalArgumentException.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), "INVALID_ARGUMENT"));
    }

    /**
     * Handle IllegalStateException.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        
        log.warn("Illegal state: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage(), "ILLEGAL_STATE"));
    }

    /**
     * Handle NullPointerException.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(
            NullPointerException ex, WebRequest request) {
        
        log.error("Null pointer exception: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error", "INTERNAL_ERROR"));
    }

    // =============================================================================
    // Generic Exception Handler
    // =============================================================================

    /**
     * Handle all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, WebRequest request) {
        
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred", "INTERNAL_ERROR"));
    }

    // =============================================================================
    // Helper Methods
    // =============================================================================

    /**
     * Create error details map.
     */
    private Map<String, Object> createErrorDetails(Exception ex, WebRequest request) {
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", LocalDateTime.now());
        details.put("path", request.getDescription(false));
        details.put("exception", ex.getClass().getSimpleName());
        
        if (ex instanceof NotificationException) {
            NotificationException ne = (NotificationException) ex;
            details.put("errorCode", ne.getErrorCode());
            details.put("errorType", ne.getErrorType());
        }
        
        return details;
    }
}
