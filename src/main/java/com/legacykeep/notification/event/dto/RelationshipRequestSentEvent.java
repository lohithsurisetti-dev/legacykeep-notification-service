package com.legacykeep.notification.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event DTO for relationship request sent events.
 * This event is published when a user sends a relationship request to another user.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipRequestSentEvent {
    
    /**
     * Unique identifier for the event
     */
    private String eventId;
    
    /**
     * Relationship ID
     */
    private Long relationshipId;
    
    /**
     * User ID of the person who sent the request
     */
    private Long requesterUserId;
    
    /**
     * User ID of the person who received the request
     */
    private Long recipientUserId;
    
    /**
     * Relationship type ID
     */
    private Long relationshipTypeId;
    
    /**
     * Relationship type name (e.g., "Father", "Son", "Friend")
     */
    private String relationshipTypeName;
    
    /**
     * Optional message from the requester
     */
    private String requestMessage;
    
    /**
     * Context ID (e.g., family circle ID)
     */
    private Long contextId;
    
    /**
     * Current status of the relationship
     */
    private String relationshipStatus;
    
    /**
     * Timestamp when the request was sent
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * Source service that published this event
     */
    private String sourceService;
    
    /**
     * Event type identifier
     */
    @Builder.Default
    private String eventType = "RELATIONSHIP_REQUEST_SENT";
    
    /**
     * Event version for backward compatibility
     */
    @Builder.Default
    private String eventVersion = "1.0";
}

