package com.legacykeep.notification.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event DTO for user email verification events.
 * This event is published when a user verifies their email address.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailVerifiedEvent {
    
    /**
     * Unique identifier for the event
     */
    private String eventId;
    
    /**
     * User ID of the user who verified their email
     */
    private Long userId;
    
    /**
     * Email address that was verified
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
     * Timestamp when the email was verified
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime verifiedAt;
    
    /**
     * Source service that published this event
     */
    private String sourceService;
    
    /**
     * Event type identifier
     */
    @Builder.Default
    private String eventType = "USER_EMAIL_VERIFIED";
}
