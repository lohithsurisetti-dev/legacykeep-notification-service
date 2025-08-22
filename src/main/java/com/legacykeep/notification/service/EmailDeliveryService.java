package com.legacykeep.notification.service;

import com.legacykeep.notification.dto.event.NotificationEventDto;
import com.legacykeep.notification.entity.Notification;
import com.legacykeep.notification.entity.NotificationDelivery;
import com.legacykeep.notification.entity.NotificationTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Base64;
import java.util.Map;

/**
 * Comprehensive Email Delivery Service
 * 
 * Provides robust email delivery with dynamic configuration,
 * template processing, and production-ready features.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailDeliveryService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final NotificationTemplateService templateService;

    // =============================================================================
    // Configuration Properties
    // =============================================================================

    @Value("${notification.email.sender.name:LegacyKeep}")
    private String defaultSenderName;

    @Value("${notification.email.sender.address:noreply@legacykeep.com}")
    private String defaultSenderAddress;

    @Value("${notification.email.reply-to:support@legacykeep.com}")
    private String defaultReplyTo;

    @Value("${notification.email.template.type:HTML}")
    private String defaultTemplateType;

    @Value("${notification.email.max-attachments:5}")
    private int maxAttachments;

    @Value("${notification.email.max-attachment-size:10485760}") // 10MB
    private long maxAttachmentSize;

    @Value("${notification.email.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${notification.email.rate-limit.max-per-minute:60}")
    private int maxEmailsPerMinute;

    // =============================================================================
    // Email Delivery Methods
    // =============================================================================

    /**
     * Send email notification using Notification entity.
     */
    public boolean sendEmail(Notification notification, NotificationDelivery delivery) {
        log.info("Sending email notification: id={}, to={}", 
                notification.getId(), notification.getRecipientEmail());

        try {
            // Validate email notification
            if (!isValidEmailNotification(notification)) {
                log.error("Invalid email notification: id={}", notification.getId());
                return false;
            }

            // Create MimeMessage
            MimeMessage message = createMimeMessage(notification);

            // Send email
            mailSender.send(message);

            log.info("Email sent successfully: id={}, to={}", 
                    notification.getId(), notification.getRecipientEmail());

            return true;

        } catch (Exception e) {
            log.error("Failed to send email: id={}, error={}", 
                    notification.getId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send email notification using NotificationEventDto (for Kafka events).
     */
    public boolean sendEmailFromEvent(NotificationEventDto eventDto) {
        log.info("Sending email from event: eventId={}, to={}", 
                eventDto.getEventId(), eventDto.getRecipientEmail());

        try {
            // Validate email event
            if (!eventDto.isValidForEmail()) {
                log.error("Invalid email event: eventId={}", eventDto.getEventId());
                return false;
            }

            // Get template
            NotificationTemplate template = templateService.getTemplateByTemplateId(eventDto.getTemplateId());

            // Create MimeMessage from event
            MimeMessage message = createMimeMessageFromEvent(eventDto, template);

            // Send email
            mailSender.send(message);

            log.info("Email sent successfully from event: eventId={}, to={}", 
                    eventDto.getEventId(), eventDto.getRecipientEmail());

            return true;

        } catch (Exception e) {
            log.error("Failed to send email from event: eventId={}, error={}", 
                    eventDto.getEventId(), e.getMessage(), e);
            return false;
        }
    }

    // =============================================================================
    // MimeMessage Creation
    // =============================================================================

    /**
     * Create MimeMessage from Notification entity.
     */
    private MimeMessage createMimeMessage(Notification notification) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set basic email properties
        helper.setTo(notification.getRecipientEmail());
        helper.setSubject(notification.getSubject());
        helper.setText(notification.getContent(), true); // HTML content

        // Set sender information
        helper.setFrom(getSenderAddress(notification.getMetadata()));

        // Set reply-to if available
        String replyTo = getReplyToAddress(notification.getMetadata());
        if (replyTo != null) {
            helper.setReplyTo(replyTo);
        }

        // Add attachments if available
        addAttachments(helper, notification.getMetadata());

        return message;
    }

    /**
     * Create MimeMessage from NotificationEventDto.
     */
    private MimeMessage createMimeMessageFromEvent(NotificationEventDto eventDto, NotificationTemplate template) 
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Process template with variables
        Map<String, Object> variables = eventDto.getAllTemplateVariables();
        String processedSubject = processTemplate(eventDto.getCustomSubject() != null ? 
                eventDto.getCustomSubject() : template.getSubjectTemplate(), variables);
        String processedContent = processTemplate(eventDto.getCustomContent() != null ? 
                eventDto.getCustomContent() : template.getTemplateContent(), variables);

        // Set basic email properties
        helper.setTo(eventDto.getRecipientEmail());
        helper.setSubject(processedSubject);
        helper.setText(processedContent, "HTML".equalsIgnoreCase(eventDto.getEmailTemplateType()));

        // Set sender information
        helper.setFrom(getSenderAddressFromEvent(eventDto));

        // Set reply-to if available
        if (eventDto.getEmailReplyTo() != null) {
            helper.setReplyTo(eventDto.getEmailReplyTo());
        }

        // Add attachments if available
        addAttachmentsFromEvent(helper, eventDto);

        // Add custom headers if available
        addCustomHeaders(helper, eventDto);

        return message;
    }

    // =============================================================================
    // Template Processing
    // =============================================================================

    /**
     * Process template with variables using Thymeleaf.
     */
    private String processTemplate(String template, Map<String, Object> variables) {
        if (template == null || template.trim().isEmpty()) {
            return "";
        }

        try {
            Context context = new Context();
            if (variables != null) {
                context.setVariables(variables);
            }

            return templateEngine.process(template, context);
        } catch (Exception e) {
            log.error("Error processing template: {}", e.getMessage(), e);
            return template; // Return original template if processing fails
        }
    }

    /**
     * Process template with variables using simple replacement (fallback).
     */
    private String processTemplateSimple(String template, Map<String, Object> variables) {
        if (template == null || template.trim().isEmpty()) {
            return "";
        }

        if (variables == null || variables.isEmpty()) {
            return template;
        }

        String processedTemplate = template;

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            processedTemplate = processedTemplate.replace(placeholder, value);
        }

        return processedTemplate;
    }

    // =============================================================================
    // Sender Configuration
    // =============================================================================

    /**
     * Get sender address from notification metadata.
     */
    private String getSenderAddress(String metadata) {
        // TODO: Parse metadata JSON to extract sender information
        // For now, use default sender
        return String.format("%s <%s>", defaultSenderName, defaultSenderAddress);
    }

    /**
     * Get sender address from event DTO.
     */
    private String getSenderAddressFromEvent(NotificationEventDto eventDto) {
        String senderName = eventDto.getEmailSenderName() != null ? 
                eventDto.getEmailSenderName() : defaultSenderName;
        String senderAddress = eventDto.getEmailSenderAddress() != null ? 
                eventDto.getEmailSenderAddress() : defaultSenderAddress;

        return String.format("%s <%s>", senderName, senderAddress);
    }

    /**
     * Get reply-to address from notification metadata.
     */
    private String getReplyToAddress(String metadata) {
        // TODO: Parse metadata JSON to extract reply-to information
        // For now, use default reply-to
        return defaultReplyTo;
    }

    // =============================================================================
    // Attachment Handling
    // =============================================================================

    /**
     * Add attachments to email from notification metadata.
     */
    private void addAttachments(MimeMessageHelper helper, String metadata) {
        // TODO: Parse metadata JSON to extract attachment information
        // For now, no attachments from notification entity
    }

    /**
     * Add attachments to email from event DTO.
     */
    private void addAttachmentsFromEvent(MimeMessageHelper helper, NotificationEventDto eventDto) {
        if (eventDto.getEmailAttachments() == null || eventDto.getEmailAttachments().isEmpty()) {
            return;
        }

        int attachmentCount = 0;
        for (Map.Entry<String, String> attachment : eventDto.getEmailAttachments().entrySet()) {
            try {
                if (attachmentCount >= maxAttachments) {
                    log.warn("Maximum number of attachments reached: {}", maxAttachments);
                    break;
                }

                String fileName = attachment.getKey();
                String base64Content = attachment.getValue();

                // Validate attachment size
                if (base64Content.length() > maxAttachmentSize) {
                    log.warn("Attachment too large: {} bytes", base64Content.length());
                    continue;
                }

                // Decode base64 content
                byte[] content = Base64.getDecoder().decode(base64Content);
                ByteArrayResource resource = new ByteArrayResource(content);

                helper.addAttachment(fileName, resource);
                attachmentCount++;

                log.debug("Added attachment: {}", fileName);

            } catch (Exception e) {
                log.error("Error adding attachment {}: {}", attachment.getKey(), e.getMessage(), e);
            }
        }
    }

    /**
     * Add custom headers to email.
     */
    private void addCustomHeaders(MimeMessageHelper helper, NotificationEventDto eventDto) {
        if (eventDto.getEmailHeaders() == null || eventDto.getEmailHeaders().isEmpty()) {
            return;
        }

        try {
            for (Map.Entry<String, String> header : eventDto.getEmailHeaders().entrySet()) {
                helper.getMimeMessage().addHeader(header.getKey(), header.getValue());
            }
        } catch (Exception e) {
            log.error("Error adding custom headers: {}", e.getMessage(), e);
        }
    }

    // =============================================================================
    // Validation Methods
    // =============================================================================

    /**
     * Validate email notification.
     */
    private boolean isValidEmailNotification(Notification notification) {
        return notification != null &&
               notification.getRecipientEmail() != null &&
               !notification.getRecipientEmail().trim().isEmpty() &&
               notification.getSubject() != null &&
               !notification.getSubject().trim().isEmpty() &&
               notification.getContent() != null &&
               !notification.getContent().trim().isEmpty();
    }

    /**
     * Validate email address format.
     */
    public boolean isValidEmailAddress(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Simple email validation regex
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    // =============================================================================
    // Rate Limiting
    // =============================================================================

    /**
     * Check if rate limit allows sending email.
     */
    private boolean isRateLimitExceeded() {
        if (!rateLimitEnabled) {
            return false;
        }

        // TODO: Implement rate limiting logic
        // - Use Redis or in-memory counter
        // - Track emails per minute
        // - Return true if limit exceeded

        return false;
    }

    // =============================================================================
    // Health Check Methods
    // =============================================================================

    /**
     * Check if email service is healthy.
     */
    public boolean isHealthy() {
        try {
            // Simple health check - try to create a message
            mailSender.createMimeMessage();
            return true;
        } catch (Exception e) {
            log.error("Email service health check failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get email service status.
     */
    public Map<String, Object> getServiceStatus() {
        return Map.of(
                "healthy", isHealthy(),
                "rateLimitEnabled", rateLimitEnabled,
                "maxEmailsPerMinute", maxEmailsPerMinute,
                "maxAttachments", maxAttachments,
                "maxAttachmentSize", maxAttachmentSize,
                "defaultSender", defaultSenderAddress,
                "defaultReplyTo", defaultReplyTo
        );
    }

    // =============================================================================
    // Configuration Methods
    // =============================================================================

    /**
     * Update email configuration dynamically.
     */
    public void updateConfiguration(String senderName, String senderAddress, String replyTo) {
        this.defaultSenderName = senderName != null ? senderName : this.defaultSenderName;
        this.defaultSenderAddress = senderAddress != null ? senderAddress : this.defaultSenderAddress;
        this.defaultReplyTo = replyTo != null ? replyTo : this.defaultReplyTo;

        log.info("Email configuration updated: sender={}, replyTo={}", 
                this.defaultSenderAddress, this.defaultReplyTo);
    }

    /**
     * Get current email configuration.
     */
    public Map<String, String> getConfiguration() {
        return Map.of(
                "senderName", defaultSenderName,
                "senderAddress", defaultSenderAddress,
                "replyTo", defaultReplyTo,
                "templateType", defaultTemplateType
        );
    }
}
