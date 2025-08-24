package com.legacykeep.notification.event.consumer;

import com.legacykeep.notification.event.dto.UserEmailVerifiedEvent;
import com.legacykeep.notification.event.dto.UserPasswordResetRequestedEvent;
import com.legacykeep.notification.event.dto.UserRegisteredEvent;
import com.legacykeep.notification.event.dto.UserEmailVerificationRequestedEvent;
import com.legacykeep.notification.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka Consumer for User Events.
 * 
 * Handles user-related events and triggers appropriate email notifications
 * using Thymeleaf templates.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventsConsumer {

    private final EmailTemplateService emailTemplateService;

    /**
     * Handle user registration events.
     * Sends welcome email using Thymeleaf template.
     */
    @KafkaListener(
        topics = "${kafka.topics.user-registered:user.registered}",
        groupId = "${spring.kafka.consumer.group-id:notification-service-group}"
    )
    public void handleUserRegistered(Map<String, Object> eventMap) {
        try {
            log.info("Received user registration event: {}", eventMap.get("eventId"));
            
            // Convert Map to UserRegisteredEvent DTO
            UserRegisteredEvent event = UserRegisteredEvent.builder()
                .eventId((String) eventMap.get("eventId"))
                .userId(((Number) eventMap.get("userId")).longValue())
                .email((String) eventMap.get("email"))
                .username((String) eventMap.get("username"))
                .firstName((String) eventMap.get("firstName"))
                .lastName((String) eventMap.get("lastName"))
                .fullName((String) eventMap.get("fullName"))
                .sourceService((String) eventMap.get("sourceService"))
                .eventType((String) eventMap.get("eventType"))
                .build();
            
            // Note: Welcome email will be sent after email verification
            log.info("User registration processed successfully for user: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to process user registration event: {}", eventMap.get("eventId"), e);
            // In production, you might want to send to a dead letter queue or retry
        }
    }

    /**
     * Handle user email verification requested events.
     * Sends email verification email using Thymeleaf template.
     */
    @KafkaListener(
        topics = "${kafka.topics.user-email-verification-requested:user.email.verification.requested}",
        groupId = "${spring.kafka.consumer.group-id:notification-service-group}"
    )
    public void handleUserEmailVerificationRequested(Map<String, Object> eventMap) {
        try {
            log.info("Received user email verification requested event: {}", eventMap.get("eventId"));
            
            // Convert Map to UserEmailVerificationRequestedEvent DTO
            UserEmailVerificationRequestedEvent event = UserEmailVerificationRequestedEvent.builder()
                .eventId((String) eventMap.get("eventId"))
                .userId(((Number) eventMap.get("userId")).longValue())
                .email((String) eventMap.get("email"))
                .username((String) eventMap.get("username"))
                .firstName((String) eventMap.get("firstName"))
                .lastName((String) eventMap.get("lastName"))
                .fullName((String) eventMap.get("fullName"))
                .verificationToken((String) eventMap.get("verificationToken"))
                .sourceService((String) eventMap.get("sourceService"))
                .eventType((String) eventMap.get("eventType"))
                .build();
            
            // Send email verification email using Thymeleaf template
            emailTemplateService.sendEmailVerificationEmail(event);
            
            log.info("Email verification email sent successfully for user: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to process user email verification requested event: {}", eventMap.get("eventId"), e);
            // In production, you might want to send to a dead letter queue or retry
        }
    }

    /**
     * Handle user email verification events.
     * Sends welcome email and verification confirmation using Thymeleaf template.
     */
    @KafkaListener(
        topics = "${kafka.topics.user-email-verified:user.email.verified}",
        groupId = "${spring.kafka.consumer.group-id:notification-service-group}"
    )
    public void handleUserEmailVerified(UserEmailVerifiedEvent event) {
        try {
            log.info("Received user email verification event: {}", event.getEventId());
            
            // Send welcome email (onboarding) using Thymeleaf template
            emailTemplateService.sendWelcomeEmailAfterVerification(event);
            
            // Send email verification confirmation using Thymeleaf template
            emailTemplateService.sendEmailVerificationConfirmation(event);
            
            log.info("Welcome email and verification confirmation sent successfully for user: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to process user email verification event: {}", event.getEventId(), e);
        }
    }

    /**
     * Handle user password reset request events.
     * Sends password reset email using Thymeleaf template.
     */
    @KafkaListener(
        topics = "${kafka.topics.user-password-reset-requested:user.password.reset.requested}",
        groupId = "${spring.kafka.consumer.group-id:notification-service-group}"
    )
    public void handleUserPasswordResetRequested(UserPasswordResetRequestedEvent event) {
        try {
            log.info("Received user password reset request event: {}", event.getEventId());
            
            // Send password reset email using Thymeleaf template
            emailTemplateService.sendPasswordResetEmail(event);
            
            log.info("Password reset email sent successfully for user: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to process user password reset request event: {}", event.getEventId(), e);
        }
    }
}
