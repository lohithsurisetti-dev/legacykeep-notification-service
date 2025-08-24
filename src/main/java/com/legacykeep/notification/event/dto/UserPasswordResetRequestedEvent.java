package com.legacykeep.notification.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event DTO for user password reset request events.
 * This event is published when a user requests a password reset.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordResetRequestedEvent {
    
    /**
     * Unique identifier for the event
     */
    private String eventId;
    
    /**
     * User ID of the user who requested password reset
     */
    private Long userId;
    
    /**
     * Email address of the user
     */
    private String email;
    
    /**
     * Username of the user
     */
    private String username;
    
    /**
     * First name of the user
     */
    private String firstName;
    
    /**
     * Last name of the user
     */
    private String lastName;
    
    /**
     * Full name of the user
     */
    private String fullName;
    
    /**
     * Password reset token
     */
    private String resetToken;
    
    /**
     * Timestamp when the password reset was requested
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestedAt;
    
    /**
     * Expiration timestamp for the reset token
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;
    
    /**
     * Source service that published this event
     */
    private String sourceService;
    
    /**
     * Event type identifier
     */
    @Builder.Default
    private String eventType = "USER_PASSWORD_RESET_REQUESTED";
}
