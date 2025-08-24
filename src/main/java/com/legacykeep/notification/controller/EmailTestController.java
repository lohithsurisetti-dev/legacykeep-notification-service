package com.legacykeep.notification.controller;

import com.legacykeep.notification.dto.ApiResponse;
import com.legacykeep.notification.service.EmailDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Email Testing", description = "APIs for testing email functionality and delivery")
public class EmailTestController {

    private final JavaMailSender mailSender;
    private final EmailDeliveryService emailDeliveryService;

    /**
     * Test email configuration health
     */
    @Operation(
        summary = "Test Email Configuration Health",
        description = "Checks if the email configuration is working properly"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email configuration is healthy"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Email configuration failed")
    })
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
    @Operation(
        summary = "Send Simple Test Email",
        description = "Sends a simple text email for testing purposes"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email sent successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request - missing required fields"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failed to send email")
    })
    @PostMapping("/send-simple")
    public ResponseEntity<ApiResponse> sendSimpleTestEmail(
        @Parameter(description = "Email request containing to, subject, and message", required = true)
        @RequestBody Map<String, String> request) {
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
    @Operation(
        summary = "Send HTML Test Email",
        description = "Sends an HTML email for testing purposes"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "HTML email sent successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request - missing required fields"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failed to send email")
    })
    @PostMapping("/send-mime")
    public ResponseEntity<ApiResponse> sendMimeTestEmail(
        @Parameter(description = "Email request containing to, subject, and htmlContent", required = true)
        @RequestBody Map<String, String> request) {
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
