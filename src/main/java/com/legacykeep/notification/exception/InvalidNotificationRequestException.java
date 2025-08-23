package com.legacykeep.notification.exception;

/**
 * Exception thrown when a notification request is invalid.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class InvalidNotificationRequestException extends NotificationException {

    private final String field;
    private final String reason;

    /**
     * Constructor with field and reason.
     * 
     * @param field The field that is invalid
     * @param reason The reason for invalidity
     */
    public InvalidNotificationRequestException(String field, String reason) {
        super(String.format("Invalid notification request - %s: %s", field, reason), 
              "INVALID_REQUEST", "VALIDATION");
        this.field = field;
        this.reason = reason;
    }

    /**
     * Constructor with message.
     * 
     * @param message Error message
     */
    public InvalidNotificationRequestException(String message) {
        super(message, "INVALID_REQUEST", "VALIDATION");
        this.field = "UNKNOWN";
        this.reason = message;
    }

    /**
     * Constructor with message and cause.
     * 
     * @param message Error message
     * @param cause Root cause
     */
    public InvalidNotificationRequestException(String message, Throwable cause) {
        super(message, "INVALID_REQUEST", "VALIDATION", cause);
        this.field = "UNKNOWN";
        this.reason = message;
    }

    /**
     * Get the invalid field.
     * 
     * @return Field name
     */
    public String getField() {
        return field;
    }

    /**
     * Get the reason for invalidity.
     * 
     * @return Reason
     */
    public String getReason() {
        return reason;
    }
}
