package com.legacykeep.notification.exception;

/**
 * Exception thrown when notification delivery fails.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class DeliveryFailedException extends NotificationException {

    private final String channel;
    private final String recipient;
    private final String failureReason;

    /**
     * Constructor with channel and failure reason.
     * 
     * @param channel The delivery channel that failed
     * @param recipient The recipient identifier
     * @param failureReason The reason for failure
     */
    public DeliveryFailedException(String channel, String recipient, String failureReason) {
        super(String.format("Delivery failed via %s to %s: %s", channel, recipient, failureReason), 
              "DELIVERY_FAILED", "DELIVERY");
        this.channel = channel;
        this.recipient = recipient;
        this.failureReason = failureReason;
    }

    /**
     * Constructor with channel, recipient, failure reason, and cause.
     * 
     * @param channel The delivery channel that failed
     * @param recipient The recipient identifier
     * @param failureReason The reason for failure
     * @param cause Root cause
     */
    public DeliveryFailedException(String channel, String recipient, String failureReason, Throwable cause) {
        super(String.format("Delivery failed via %s to %s: %s", channel, recipient, failureReason), 
              "DELIVERY_FAILED", "DELIVERY", cause);
        this.channel = channel;
        this.recipient = recipient;
        this.failureReason = failureReason;
    }

    /**
     * Constructor with message.
     * 
     * @param message Error message
     */
    public DeliveryFailedException(String message) {
        super(message, "DELIVERY_FAILED", "DELIVERY");
        this.channel = "UNKNOWN";
        this.recipient = "UNKNOWN";
        this.failureReason = message;
    }

    /**
     * Constructor with message and cause.
     * 
     * @param message Error message
     * @param cause Root cause
     */
    public DeliveryFailedException(String message, Throwable cause) {
        super(message, "DELIVERY_FAILED", "DELIVERY", cause);
        this.channel = "UNKNOWN";
        this.recipient = "UNKNOWN";
        this.failureReason = message;
    }

    /**
     * Get the delivery channel that failed.
     * 
     * @return Channel name
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Get the recipient identifier.
     * 
     * @return Recipient identifier
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Get the failure reason.
     * 
     * @return Failure reason
     */
    public String getFailureReason() {
        return failureReason;
    }
}
