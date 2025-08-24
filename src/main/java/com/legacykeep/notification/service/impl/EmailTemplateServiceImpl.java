package com.legacykeep.notification.service.impl;

import com.legacykeep.notification.event.dto.UserEmailVerifiedEvent;
import com.legacykeep.notification.event.dto.UserPasswordResetRequestedEvent;
import com.legacykeep.notification.event.dto.UserRegisteredEvent;
import com.legacykeep.notification.event.dto.UserEmailVerificationRequestedEvent;
import com.legacykeep.notification.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of EmailTemplateService using Thymeleaf templates.
 * 
 * Processes email templates and sends emails for various user events.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Value("${notification.email.smtp.username}")
    private String fromEmail;

    @Value("${notification.email.sender.name:LegacyKeep}")
    private String fromName;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendWelcomeEmail(UserRegisteredEvent event) {
        try {
            log.info("Sending welcome email to: {}", event.getEmail());

            // Prepare template context
            Context context = new Context();
            context.setVariable("user", event);
            context.setVariable("fullName", event.getFullName());
            context.setVariable("username", event.getUsername());
            context.setVariable("email", event.getEmail());
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("loginUrl", frontendUrl + "/login");
            context.setVariable("dashboardUrl", frontendUrl + "/dashboard");

            // Process template
            String htmlContent = templateEngine.process("email/auth/welcome", context);

            // Send email
            sendHtmlEmail(
                event.getEmail(),
                "🎉 Welcome to LegacyKeep!",
                htmlContent
            );

            log.info("Welcome email sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", event.getEmail(), e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    @Override
    public void sendWelcomeEmailAfterVerification(UserEmailVerifiedEvent event) {
        try {
            log.info("Sending welcome email after verification to: {}", event.getEmail());

            // Prepare template context
            Context context = new Context();
            context.setVariable("user", event);
            context.setVariable("fullName", event.getFullName());
            context.setVariable("username", event.getUsername());
            context.setVariable("email", event.getEmail());
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("loginUrl", frontendUrl + "/login");
            context.setVariable("dashboardUrl", frontendUrl + "/dashboard");

            // Process template
            String htmlContent = templateEngine.process("email/auth/welcome", context);

            // Send email
            sendHtmlEmail(
                event.getEmail(),
                "🎉 Welcome to LegacyKeep!",
                htmlContent
            );

            log.info("Welcome email after verification sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email after verification to: {}", event.getEmail(), e);
            throw new RuntimeException("Failed to send welcome email after verification", e);
        }
    }

    @Override
    public void sendEmailVerificationEmail(UserEmailVerificationRequestedEvent event) {
        try {
            log.info("Sending email verification email to: {}", event.getEmail());

            // Prepare template context
            Context context = new Context();
            context.setVariable("user", event);
            context.setVariable("fullName", event.getFullName());
            context.setVariable("username", event.getUsername());
            context.setVariable("email", event.getEmail());
            context.setVariable("verificationToken", event.getVerificationToken());
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("verificationUrl", frontendUrl + "/verify-email?token=" + event.getVerificationToken());
            // Calculate expiration hours
            long expiryHours = 24; // Default for email verification
            if (event.getExpiresAt() != null) {
                long hoursUntilExpiry = java.time.Duration.between(
                    java.time.LocalDateTime.now(), 
                    event.getExpiresAt()
                ).toHours();
                expiryHours = Math.max(1, hoursUntilExpiry); // Minimum 1 hour
            }
            context.setVariable("expiryHours", expiryHours);

            // Process template
            String htmlContent = templateEngine.process("email/auth/email-verification", context);

            // Send email
            sendHtmlEmail(
                event.getEmail(),
                "📧 Verify Your Email - LegacyKeep",
                htmlContent
            );

            log.info("Email verification email sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email verification email to: {}", event.getEmail(), e);
            throw new RuntimeException("Failed to send email verification email", e);
        }
    }

    @Override
    public void sendEmailVerificationConfirmation(UserEmailVerifiedEvent event) {
        try {
            log.info("Sending email verification confirmation to: {}", event.getEmail());

            // Prepare template context
            Context context = new Context();
            context.setVariable("user", event);
            context.setVariable("fullName", event.getFullName());
            context.setVariable("username", event.getUsername());
            context.setVariable("email", event.getEmail());
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("dashboardUrl", frontendUrl + "/dashboard");

            // Process template
            String htmlContent = templateEngine.process("email/auth/email-verification", context);

            // Send email
            sendHtmlEmail(
                event.getEmail(),
                "✅ Email Verified - Welcome to LegacyKeep!",
                htmlContent
            );

            log.info("Email verification confirmation sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email verification confirmation to: {}", event.getEmail(), e);
            throw new RuntimeException("Failed to send email verification confirmation", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(UserPasswordResetRequestedEvent event) {
        try {
            log.info("Sending password reset email to: {}", event.getEmail());

            // Prepare template context
            Context context = new Context();
            context.setVariable("user", event);
            context.setVariable("fullName", event.getFullName());
            context.setVariable("username", event.getUsername());
            context.setVariable("email", event.getEmail());
            context.setVariable("resetToken", event.getResetToken());
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("resetUrl", frontendUrl + "/reset-password?token=" + event.getResetToken());
            // Calculate expiration hours
            long expiryHours = 1; // Default for password reset
            if (event.getExpiresAt() != null) {
                long hoursUntilExpiry = java.time.Duration.between(
                    java.time.LocalDateTime.now(), 
                    event.getExpiresAt()
                ).toHours();
                expiryHours = Math.max(1, hoursUntilExpiry); // Minimum 1 hour
            }
            context.setVariable("expiryHours", expiryHours);

            // Process template
            String htmlContent = templateEngine.process("email/auth/password-reset", context);

            // Send email
            sendHtmlEmail(
                event.getEmail(),
                "🔑 Reset Your LegacyKeep Password",
                htmlContent
            );

            log.info("Password reset email sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", event.getEmail(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    /**
     * Send HTML email using JavaMailSender.
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML content of the email
     * @throws MessagingException if email sending fails
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true indicates HTML content

        mailSender.send(message);
        log.debug("HTML email sent to: {} with subject: {}", to, subject);
    }
}
