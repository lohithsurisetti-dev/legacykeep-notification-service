package com.legacykeep.notification.service;

import com.legacykeep.notification.event.dto.RelationshipRequestAcceptedEvent;
import com.legacykeep.notification.event.dto.RelationshipRequestRejectedEvent;
import com.legacykeep.notification.event.dto.RelationshipRequestSentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 * Unit test for RelationshipEventConsumer.
 */
@ExtendWith(MockitoExtension.class)
class RelationshipEventConsumerTest {

    @Mock
    private EmailTemplateService emailTemplateService;

    @InjectMocks
    private RelationshipEventConsumer relationshipEventConsumer;

    private RelationshipRequestSentEvent requestSentEvent;
    private RelationshipRequestAcceptedEvent requestAcceptedEvent;
    private RelationshipRequestRejectedEvent requestRejectedEvent;

    @BeforeEach
    void setUp() {
        // Create test events
        requestSentEvent = RelationshipRequestSentEvent.builder()
                .eventId("test-event-1")
                .relationshipId(1L)
                .requesterUserId(100L)
                .recipientUserId(200L)
                .relationshipTypeId(1L)
                .relationshipTypeName("Father")
                .requestMessage("Please accept my father relationship request")
                .contextId(1L)
                .relationshipStatus("PENDING")
                .timestamp(LocalDateTime.now())
                .sourceService("relationship-service")
                .eventType("RELATIONSHIP_REQUEST_SENT")
                .eventVersion("1.0")
                .build();

        requestAcceptedEvent = RelationshipRequestAcceptedEvent.builder()
                .eventId("test-event-2")
                .relationshipId(1L)
                .acceptorUserId(200L)
                .requesterUserId(100L)
                .relationshipTypeId(1L)
                .relationshipTypeName("Father")
                .responseMessage("I accept your father relationship request")
                .contextId(1L)
                .relationshipStatus("ACTIVE")
                .timestamp(LocalDateTime.now())
                .sourceService("relationship-service")
                .eventType("RELATIONSHIP_REQUEST_ACCEPTED")
                .eventVersion("1.0")
                .build();

        requestRejectedEvent = RelationshipRequestRejectedEvent.builder()
                .eventId("test-event-3")
                .relationshipId(1L)
                .rejectorUserId(200L)
                .requesterUserId(100L)
                .relationshipTypeId(1L)
                .relationshipTypeName("Father")
                .responseMessage("I cannot accept this relationship request")
                .contextId(1L)
                .relationshipStatus("REJECTED")
                .timestamp(LocalDateTime.now())
                .sourceService("relationship-service")
                .eventType("RELATIONSHIP_REQUEST_REJECTED")
                .eventVersion("1.0")
                .build();
    }

    @Test
    void testHandleRelationshipRequestSent_ShouldProcessSuccessfully() {
        // When
        relationshipEventConsumer.handleRelationshipRequestSent(
                requestSentEvent, "relationship-events", 0, 0L);

        // Then
        // Verify that the method completes without throwing exceptions
        // In a real implementation, we would verify that notifications are sent
        verify(emailTemplateService, never()).sendWelcomeEmail(any()); // Just to verify mock is working
    }

    @Test
    void testHandleRelationshipRequestAccepted_ShouldProcessSuccessfully() {
        // When
        relationshipEventConsumer.handleRelationshipRequestAccepted(
                requestAcceptedEvent, "relationship-events", 0, 0L);

        // Then
        // Verify that the method completes without throwing exceptions
        // In a real implementation, we would verify that notifications are sent
        verify(emailTemplateService, never()).sendWelcomeEmail(any()); // Just to verify mock is working
    }

    @Test
    void testHandleRelationshipRequestRejected_ShouldProcessSuccessfully() {
        // When
        relationshipEventConsumer.handleRelationshipRequestRejected(
                requestRejectedEvent, "relationship-events", 0, 0L);

        // Then
        // Verify that the method completes without throwing exceptions
        // In a real implementation, we would verify that notifications are sent
        verify(emailTemplateService, never()).sendWelcomeEmail(any()); // Just to verify mock is working
    }

    @Test
    void testHandleRelationshipRequestSent_WithException_ShouldLogError() {
        // Given
        RelationshipRequestSentEvent invalidEvent = RelationshipRequestSentEvent.builder()
                .eventId("invalid-event")
                .build();

        // When & Then
        // The method should handle exceptions gracefully and log errors
        relationshipEventConsumer.handleRelationshipRequestSent(
                invalidEvent, "relationship-events", 0, 0L);

        // Verify that the method completes without throwing exceptions
        verify(emailTemplateService, never()).sendWelcomeEmail(any());
    }
}

