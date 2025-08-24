package com.legacykeep.notification.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event DTO for user registration events.
 * This event is published when a new user registers in the system.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    
    /**
     * Unique identifier for the event
     */
    private String eventId;
    
    /**
     * User ID of the registered user
     */
    private Long userId;
    
    /**
     * Email address of the registered user
     */
    private String email;
    
    /**
     * Username of the registered user
     */
    private String username;
    
    /**
     * First name of the registered user
     */
    private String firstName;
    
    /**
     * Last name of the registered user
     */
    private String lastName;
    
    /**
     * Full name of the registered user
     */
    private String fullName;
    
    /**
     * Timestamp when the user was registered
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registeredAt;
    
    /**
     * Source service that published this event
     */
    private String sourceService;
    
    /**
     * Event type identifier
     */
    @Builder.Default
    private String eventType = "USER_REGISTERED";
}
