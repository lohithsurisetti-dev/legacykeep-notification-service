package com.legacykeep.notification.controller;

import com.legacykeep.notification.dto.ApiResponse;
import com.legacykeep.notification.dto.request.SendNotificationRequest;
import com.legacykeep.notification.dto.response.NotificationResponse;
import com.legacykeep.notification.entity.NotificationChannel;
import com.legacykeep.notification.entity.NotificationType;
import com.legacykeep.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Event Simulator Controller
 * 
 * Simulates Kafka events for testing the complete notification flow.
 * This controller should be disabled in production.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/simulator")
@RequiredArgsConstructor
@Slf4j
public class KafkaEventSimulatorController {

    private final NotificationService notificationService;

    /**
     * Simulate user registration event with email verification.
     * 
     * @param request Simulation request
     * @return Simulation result
     */
    @PostMapping("/user-registration")
    public ResponseEntity<ApiResponse<String>> simulateUserRegistration(@RequestBody SimulateUserRegistrationRequest request) {
        
        log.info("Simulating user registration event for: {}", request.getEmail());

        try {
            // Create notification request
            SendNotificationRequest notificationRequest = SendNotificationRequest.builder()
                    .recipientId(9999L) // Default test user ID
                    .recipientEmail(request.getEmail())
                    .notificationType(NotificationType.EMAIL)
                    .channel(NotificationChannel.EMAIL)
                    .templateId("email-verification")
                    .templateVariables(createEmailVerificationVariables(request))
                    .maxRetries(3)
                    .build();

            // Process notification
            NotificationResponse response = notificationService.sendNotification(notificationRequest);

            return ResponseEntity.ok(ApiResponse.success(
                    "User registration event simulated successfully",
                    "Event ID: " + response.getEventId()
            ));

        } catch (Exception e) {
            log.error("Error simulating user registration event: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Failed to simulate user registration event",
                    "SIMULATION_ERROR",
                    e.getMessage()
            ));
        }
    }

    /**
     * Simulate password reset event.
     * 
     * @param request Simulation request
     * @return Simulation result
     */
    @PostMapping("/password-reset")
    public ResponseEntity<ApiResponse<String>> simulatePasswordReset(@RequestBody SimulatePasswordResetRequest request) {
        
        log.info("Simulating password reset event for: {}", request.getEmail());

        try {
            // Create notification request
            SendNotificationRequest notificationRequest = SendNotificationRequest.builder()
                    .recipientId(9999L) // Default test user ID
                    .recipientEmail(request.getEmail())
                    .notificationType(NotificationType.EMAIL)
                    .channel(NotificationChannel.EMAIL)
                    .templateId("password-reset")
                    .templateVariables(createPasswordResetVariables(request))
                    .maxRetries(3)
                    .build();

            // Process notification
            NotificationResponse response = notificationService.sendNotification(notificationRequest);

            return ResponseEntity.ok(ApiResponse.success(
                    "Password reset event simulated successfully",
                    "Event ID: " + response.getEventId()
            ));

        } catch (Exception e) {
            log.error("Error simulating password reset event: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Failed to simulate password reset event",
                    "SIMULATION_ERROR",
                    e.getMessage()
            ));
        }
    }

    /**
     * Simulate welcome email event.
     * 
     * @param request Simulation request
     * @return Simulation result
     */
    @PostMapping("/welcome-email")
    public ResponseEntity<ApiResponse<String>> simulateWelcomeEmail(@RequestBody SimulateWelcomeEmailRequest request) {
        
        log.info("Simulating welcome email event for: {}", request.getEmail());

        try {
            // Create notification request
            SendNotificationRequest notificationRequest = SendNotificationRequest.builder()
                    .recipientId(9999L) // Default test user ID
                    .recipientEmail(request.getEmail())
                    .notificationType(NotificationType.EMAIL)
                    .channel(NotificationChannel.EMAIL)
                    .templateId("welcome")
                    .templateVariables(createWelcomeEmailVariables(request))
                    .maxRetries(3)
                    .build();

            // Process notification
            NotificationResponse response = notificationService.sendNotification(notificationRequest);

            return ResponseEntity.ok(ApiResponse.success(
                    "Welcome email event simulated successfully",
                    "Event ID: " + response.getEventId()
            ));

        } catch (Exception e) {
            log.error("Error simulating welcome email event: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Failed to simulate welcome email event",
                    "SIMULATION_ERROR",
                    e.getMessage()
            ));
        }
    }

    // =============================================================================
    // Helper Methods
    // =============================================================================

    private Map<String, Object> createEmailVerificationVariables(SimulateUserRegistrationRequest request) {
        Map<String, Object> variables = new HashMap<>();
        // Set default username if not provided
        String userName = (request.getUserName() != null && !request.getUserName().trim().isEmpty()) 
            ? request.getUserName() 
            : "LegacyKeep User";
        variables.put("userName", userName);
        variables.put("verificationUrl", "https://legacykeep.com/verify?token=test-verification-token-123");
        variables.put("expiryHours", 24);
        return variables;
    }

    private Map<String, Object> createPasswordResetVariables(SimulatePasswordResetRequest request) {
        Map<String, Object> variables = new HashMap<>();
        // Set default username if not provided
        String userName = (request.getUserName() != null && !request.getUserName().trim().isEmpty()) 
            ? request.getUserName() 
            : "LegacyKeep User";
        variables.put("userName", userName);
        variables.put("resetUrl", "https://legacykeep.com/reset-password?token=test-reset-token-456");
        variables.put("expiryHours", 1);
        return variables;
    }

    private Map<String, Object> createWelcomeEmailVariables(SimulateWelcomeEmailRequest request) {
        Map<String, Object> variables = new HashMap<>();
        // Set default username if not provided
        String userName = (request.getUserName() != null && !request.getUserName().trim().isEmpty()) 
            ? request.getUserName() 
            : "LegacyKeep User";
        variables.put("userName", userName);
        variables.put("dashboardUrl", "https://legacykeep.com/dashboard");
        variables.put("welcomeMessage", "Welcome to LegacyKeep! We're excited to have you on board.");
        return variables;
    }

    // =============================================================================
    // Request DTOs
    // =============================================================================

    public static class SimulateUserRegistrationRequest {
        private String recipientEmail;
        private String userName;

        // Getters and setters
        public String getEmail() { return recipientEmail; }
        public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }

    public static class SimulatePasswordResetRequest {
        private String recipientEmail;
        private String userName;

        // Getters and setters
        public String getEmail() { return recipientEmail; }
        public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }

    public static class SimulateWelcomeEmailRequest {
        private String recipientEmail;
        private String userName;

        // Getters and setters
        public String getEmail() { return recipientEmail; }
        public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }
}
