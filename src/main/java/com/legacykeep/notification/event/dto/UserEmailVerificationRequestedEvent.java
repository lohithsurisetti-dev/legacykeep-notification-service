package com.legacykeep.notification.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event DTO for user email verification request events.
 * This event is received when a user needs to verify their email address.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailVerificationRequestedEvent {
    
    /**
     * Unique identifier for the event
     */
    private String eventId;
    
    /**
     * User ID of the user requesting verification
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
     * Email verification token
     */
    private String verificationToken;
    
    /**
     * Timestamp when the verification was requested
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestedAt;
    
    /**
     * Timestamp when the verification token expires
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
    private String eventType;
}
