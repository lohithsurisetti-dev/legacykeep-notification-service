package com.legacykeep.notification.exception;

/**
 * Base exception for notification service errors.
 * 
 * All notification-related exceptions should extend this class
 * to provide consistent error handling and categorization.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class NotificationException extends RuntimeException {

    private final String errorCode;
    private final String errorType;

    /**
     * Constructor with message.
     * 
     * @param message Error message
     */
    public NotificationException(String message) {
        super(message);
        this.errorCode = "NOTIFICATION_ERROR";
        this.errorType = "GENERAL";
    }

    /**
     * Constructor with message and cause.
     * 
     * @param message Error message
     * @param cause Root cause
     */
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "NOTIFICATION_ERROR";
        this.errorType = "GENERAL";
    }

    /**
     * Constructor with message, error code, and error type.
     * 
     * @param message Error message
     * @param errorCode Specific error code
     * @param errorType Error type category
     */
    public NotificationException(String message, String errorCode, String errorType) {
        super(message);
        this.errorCode = errorCode;
        this.errorType = errorType;
    }

    /**
     * Constructor with message, error code, error type, and cause.
     * 
     * @param message Error message
     * @param errorCode Specific error code
     * @param errorType Error type category
     * @param cause Root cause
     */
    public NotificationException(String message, String errorCode, String errorType, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorType = errorType;
    }

    /**
     * Get the error code.
     * 
     * @return Error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get the error type.
     * 
     * @return Error type
     */
    public String getErrorType() {
        return errorType;
    }
}
