package com.legacykeep.notification.event.consumer;

import com.legacykeep.notification.event.dto.UserEmailVerifiedEvent;
import com.legacykeep.notification.event.dto.UserPasswordResetRequestedEvent;
import com.legacykeep.notification.event.dto.UserRegisteredEvent;
import com.legacykeep.notification.event.dto.UserOtpVerificationRequestedEvent;
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

    // Email link verification removed - using OTP verification as primary method

    // Email verification event handler removed - using OTP verification as primary method

    /**
     * Handle user OTP verification requested events.
     * Sends OTP email using Thymeleaf template.
     */
    @KafkaListener(
        topics = "${kafka.topics.user-otp-verification-requested:user.otp.verification.requested}",
        groupId = "${spring.kafka.consumer.group-id:notification-service-group}"
    )
    public void handleUserOtpVerificationRequested(Map<String, Object> eventMap) {
        try {
            log.info("Received user OTP verification requested event: {}", eventMap.get("eventId"));
            
            // Convert Map to UserOtpVerificationRequestedEvent DTO
            UserOtpVerificationRequestedEvent event = UserOtpVerificationRequestedEvent.builder()
                .eventId((String) eventMap.get("eventId"))
                .userId(((Number) eventMap.get("userId")).longValue())
                .email((String) eventMap.get("email"))
                .username((String) eventMap.get("username"))
                .firstName((String) eventMap.get("firstName"))
                .lastName((String) eventMap.get("lastName"))
                .fullName((String) eventMap.get("fullName"))
                .otpCode((String) eventMap.get("otpCode"))
                .sourceService((String) eventMap.get("sourceService"))
                .eventType((String) eventMap.get("eventType"))
                .build();
            
            // Send OTP email using Thymeleaf template
            emailTemplateService.sendOtpVerificationEmail(event);
            
            log.info("OTP verification email sent successfully for user: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to process user OTP verification requested event: {}", eventMap.get("eventId"), e);
            // In production, you might want to send to a dead letter queue or retry
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
