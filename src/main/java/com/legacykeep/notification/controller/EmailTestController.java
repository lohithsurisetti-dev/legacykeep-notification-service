package com.legacykeep.notification.controller;

import com.legacykeep.notification.dto.ApiResponse;
import com.legacykeep.notification.service.EmailDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Email Test Controller for debugging email functionality
 */
@Slf4j
@RestController
@RequestMapping("/email-test")
@RequiredArgsConstructor
public class EmailTestController {

    private final JavaMailSender mailSender;
    private final EmailDeliveryService emailDeliveryService;

    /**
     * Test email configuration health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> testEmailHealth() {
        try {
            // Test basic connection
            MimeMessage message = mailSender.createMimeMessage();
            
            return ResponseEntity.ok(ApiResponse.success(
                "Email configuration is healthy"
            ));
        } catch (Exception e) {
            log.error("Email health check failed", e);
            return ResponseEntity.ok(ApiResponse.error(
                "Email configuration failed",
                "EMAIL_CONFIG_ERROR",
                e.getMessage()
            ));
        }
    }

    /**
     * Send a simple test email
     */
    @PostMapping("/send-simple")
    public ResponseEntity<ApiResponse> sendSimpleTestEmail(@RequestBody Map<String, String> request) {
        try {
            String to = request.get("to");
            String subject = request.get("subject");
            String message = request.get("message");

            if (to == null || subject == null || message == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Missing required fields: to, subject, message",
                    "INVALID_REQUEST",
                    "All fields are required"
                ));
            }

            SimpleMailMessage simpleMessage = new SimpleMailMessage();
            simpleMessage.setTo(to);
            simpleMessage.setSubject(subject);
            simpleMessage.setText(message);
            simpleMessage.setFrom("legacykeep7@gmail.com");

            mailSender.send(simpleMessage);

            log.info("Simple test email sent successfully to: {}", to);

            return ResponseEntity.ok(ApiResponse.success(
                "Simple test email sent successfully"
            ));

        } catch (Exception e) {
            log.error("Failed to send simple test email", e);
            return ResponseEntity.ok(ApiResponse.error(
                "Failed to send simple test email",
                "EMAIL_SEND_ERROR",
                e.getMessage()
            ));
        }
    }

    /**
     * Test MimeMessage creation
     */
    @PostMapping("/send-mime")
    public ResponseEntity<ApiResponse> sendMimeTestEmail(@RequestBody Map<String, String> request) {
        try {
            String to = request.get("to");
            String subject = request.get("subject");
            String htmlContent = request.get("htmlContent");

            if (to == null || subject == null || htmlContent == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Missing required fields: to, subject, htmlContent",
                    "INVALID_REQUEST",
                    "All fields are required"
                ));
            }

            MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML content
            helper.setFrom("legacykeep7@gmail.com");

            mailSender.send(message);

            log.info("Mime test email sent successfully to: {}", to);

            return ResponseEntity.ok(ApiResponse.success(
                "Mime test email sent successfully"
            ));

        } catch (Exception e) {
            log.error("Failed to send mime test email", e);
            return ResponseEntity.ok(ApiResponse.error(
                "Failed to send mime test email",
                "EMAIL_SEND_ERROR",
                e.getMessage()
            ));
        }
    }
}
