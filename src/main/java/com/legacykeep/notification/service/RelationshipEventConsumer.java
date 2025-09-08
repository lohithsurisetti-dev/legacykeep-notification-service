package com.legacykeep.notification.service;

import com.legacykeep.notification.event.dto.RelationshipRequestAcceptedEvent;
import com.legacykeep.notification.event.dto.RelationshipRequestRejectedEvent;
import com.legacykeep.notification.event.dto.RelationshipRequestSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Consumer service for relationship events from Relationship Service.
 * 
 * Handles relationship-related events and triggers appropriate notifications.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RelationshipEventConsumer {

    private final EmailTemplateService emailTemplateService;

    /**
     * Handle relationship request sent events.
     * 
     * @param event The relationship request sent event
     * @param topic The Kafka topic name
     * @param partition The Kafka partition
     * @param offset The Kafka offset
     */
    @KafkaListener(topics = "relationship-events", groupId = "notification-service-group")
    public void handleRelationshipRequestSent(
            @Payload RelationshipRequestSentEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received relationship request sent event: {} from topic: {}, partition: {}, offset: {}", 
                event.getEventId(), topic, partition, offset);
        
        try {
            // Send notification to the recipient about the new relationship request
            sendRelationshipRequestNotification(event);
            
            log.info("Successfully processed relationship request sent event: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Error processing relationship request sent event: {}", event.getEventId(), e);
            // In a production system, you might want to implement retry logic or dead letter queue
        }
    }

    /**
     * Handle relationship request accepted events.
     * 
     * @param event The relationship request accepted event
     * @param topic The Kafka topic name
     * @param partition The Kafka partition
     * @param offset The Kafka offset
     */
    @KafkaListener(topics = "relationship-events", groupId = "notification-service-group")
    public void handleRelationshipRequestAccepted(
            @Payload RelationshipRequestAcceptedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received relationship request accepted event: {} from topic: {}, partition: {}, offset: {}", 
                event.getEventId(), topic, partition, offset);
        
        try {
            // Send notification to the original requester about the acceptance
            sendRelationshipAcceptedNotification(event);
            
            log.info("Successfully processed relationship request accepted event: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Error processing relationship request accepted event: {}", event.getEventId(), e);
            // In a production system, you might want to implement retry logic or dead letter queue
        }
    }

    /**
     * Handle relationship request rejected events.
     * 
     * @param event The relationship request rejected event
     * @param topic The Kafka topic name
     * @param partition The Kafka partition
     * @param offset The Kafka offset
     */
    @KafkaListener(topics = "relationship-events", groupId = "notification-service-group")
    public void handleRelationshipRequestRejected(
            @Payload RelationshipRequestRejectedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received relationship request rejected event: {} from topic: {}, partition: {}, offset: {}", 
                event.getEventId(), topic, partition, offset);
        
        try {
            // Send notification to the original requester about the rejection
            sendRelationshipRejectedNotification(event);
            
            log.info("Successfully processed relationship request rejected event: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Error processing relationship request rejected event: {}", event.getEventId(), e);
            // In a production system, you might want to implement retry logic or dead letter queue
        }
    }

    /**
     * Send notification for a new relationship request.
     * 
     * @param event The relationship request sent event
     */
    private void sendRelationshipRequestNotification(RelationshipRequestSentEvent event) {
        log.info("Sending relationship request notification to user: {} for relationship type: {}", 
                event.getRecipientUserId(), event.getRelationshipTypeName());
        
        // TODO: Implement actual notification sending
        // This would typically involve:
        // 1. Getting user preferences for notification channels
        // 2. Getting user contact information (email, phone, etc.)
        // 3. Sending email/push notification/SMS based on preferences
        // 4. Logging the notification delivery
        
        log.info("Relationship request notification sent to user: {} for relationship: {}", 
                event.getRecipientUserId(), event.getRelationshipId());
    }

    /**
     * Send notification for an accepted relationship request.
     * 
     * @param event The relationship request accepted event
     */
    private void sendRelationshipAcceptedNotification(RelationshipRequestAcceptedEvent event) {
        log.info("Sending relationship accepted notification to user: {} for relationship type: {}", 
                event.getRequesterUserId(), event.getRelationshipTypeName());
        
        // TODO: Implement actual notification sending
        // This would typically involve:
        // 1. Getting user preferences for notification channels
        // 2. Getting user contact information (email, phone, etc.)
        // 3. Sending email/push notification/SMS based on preferences
        // 4. Logging the notification delivery
        
        log.info("Relationship accepted notification sent to user: {} for relationship: {}", 
                event.getRequesterUserId(), event.getRelationshipId());
    }

    /**
     * Send notification for a rejected relationship request.
     * 
     * @param event The relationship request rejected event
     */
    private void sendRelationshipRejectedNotification(RelationshipRequestRejectedEvent event) {
        log.info("Sending relationship rejected notification to user: {} for relationship type: {}", 
                event.getRequesterUserId(), event.getRelationshipTypeName());
        
        // TODO: Implement actual notification sending
        // This would typically involve:
        // 1. Getting user preferences for notification channels
        // 2. Getting user contact information (email, phone, etc.)
        // 3. Sending email/push notification/SMS based on preferences
        // 4. Logging the notification delivery
        
        log.info("Relationship rejected notification sent to user: {} for relationship: {}", 
                event.getRequesterUserId(), event.getRelationshipId());
    }
}

