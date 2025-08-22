package com.legacykeep.notification.service;

import com.legacykeep.notification.dto.response.NotificationTemplateResponse;
import com.legacykeep.notification.entity.NotificationChannel;
import com.legacykeep.notification.entity.NotificationTemplate;
import com.legacykeep.notification.entity.NotificationType;
import com.legacykeep.notification.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Notification Template Service for template management and processing.
 * 
 * Provides business logic for template operations including
 * template processing, validation, and management.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final NotificationTemplateRepository templateRepository;

    // =============================================================================
    // Template Processing
    // =============================================================================

    /**
     * Process template with variables.
     * 
     * This method replaces template variables with actual values.
     * Uses simple string replacement for now, can be enhanced with
     * more sophisticated template engines.
     */
    public String processTemplate(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return template;
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

    /**
     * Process template with variables and return both subject and content.
     */
    public TemplateProcessingResult processTemplateWithSubject(NotificationTemplate template, Map<String, Object> variables) {
        String processedSubject = null;
        String processedContent = null;

        if (template.hasSubject()) {
            processedSubject = processTemplate(template.getSubjectTemplate(), variables);
        }

        if (template.hasTextContent()) {
            processedContent = processTemplate(template.getContentTemplate(), variables);
        }

        return TemplateProcessingResult.builder()
                .subject(processedSubject)
                .content(processedContent)
                .build();
    }

    // =============================================================================
    // Template Management
    // =============================================================================

    /**
     * Get template by ID.
     */
    @Transactional(readOnly = true)
    public NotificationTemplate getTemplateById(Long id) {
        log.debug("Fetching template by ID: {}", id);

        return templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with ID: " + id));
    }

    /**
     * Get template by template ID.
     */
    @Transactional(readOnly = true)
    public NotificationTemplate getTemplateByTemplateId(String templateId) {
        log.debug("Fetching template by template ID: {}", templateId);

        return templateRepository.findByTemplateIdAndIsActiveTrue(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Active template not found: " + templateId));
    }

    /**
     * Get all active templates.
     */
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getAllActiveTemplates() {
        log.debug("Fetching all active templates");

        return templateRepository.findByIsActiveTrue();
    }

    /**
     * Get templates by notification type.
     */
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getTemplatesByType(NotificationType type) {
        log.debug("Fetching templates by type: {}", type);

        return templateRepository.findByNotificationTypeAndIsActiveTrue(type);
    }

    /**
     * Get templates by channel.
     */
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getTemplatesByChannel(NotificationChannel channel) {
        log.debug("Fetching templates by channel: {}", channel);

        return templateRepository.findByChannelAndIsActiveTrue(channel);
    }

    /**
     * Get templates by type and channel.
     */
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getTemplatesByTypeAndChannel(NotificationType type, NotificationChannel channel) {
        log.debug("Fetching templates by type: {} and channel: {}", type, channel);

        return templateRepository.findByNotificationTypeAndChannelAndIsActiveTrue(type, channel);
    }

    /**
     * Search templates by name or description.
     */
    @Transactional(readOnly = true)
    public List<NotificationTemplate> searchTemplates(String searchTerm) {
        log.debug("Searching templates with term: {}", searchTerm);

        return templateRepository.findByNameOrDescriptionContainingIgnoreCase(searchTerm);
    }

    /**
     * Create new template.
     */
    @Transactional
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        log.info("Creating new template: {}", template.getTemplateId());

        // Validate template
        validateTemplate(template);

        // Check if template ID already exists
        if (templateRepository.existsByTemplateId(template.getTemplateId())) {
            throw new IllegalArgumentException("Template ID already exists: " + template.getTemplateId());
        }

        NotificationTemplate savedTemplate = templateRepository.save(template);

        log.info("Template created successfully: id={}, templateId={}", 
                savedTemplate.getId(), savedTemplate.getTemplateId());

        return savedTemplate;
    }

    /**
     * Update existing template.
     */
    @Transactional
    public NotificationTemplate updateTemplate(Long id, NotificationTemplate templateUpdates) {
        log.info("Updating template: {}", id);

        NotificationTemplate existingTemplate = getTemplateById(id);

        // Update fields
        if (templateUpdates.getName() != null) {
            existingTemplate.setName(templateUpdates.getName());
        }
        if (templateUpdates.getDescription() != null) {
            existingTemplate.setDescription(templateUpdates.getDescription());
        }
        if (templateUpdates.getSubjectTemplate() != null) {
            existingTemplate.setSubjectTemplate(templateUpdates.getSubjectTemplate());
        }
        if (templateUpdates.getContentTemplate() != null) {
            existingTemplate.setContentTemplate(templateUpdates.getContentTemplate());
        }
        if (templateUpdates.getHtmlTemplate() != null) {
            existingTemplate.setHtmlTemplate(templateUpdates.getHtmlTemplate());
        }
        if (templateUpdates.getVariables() != null) {
            existingTemplate.setVariables(templateUpdates.getVariables());
        }
        if (templateUpdates.getIsActive() != null) {
            existingTemplate.setIsActive(templateUpdates.getIsActive());
        }
        if (templateUpdates.getVersion() != null) {
            existingTemplate.setVersion(templateUpdates.getVersion());
        }

        NotificationTemplate savedTemplate = templateRepository.save(existingTemplate);

        log.info("Template updated successfully: id={}, templateId={}", 
                savedTemplate.getId(), savedTemplate.getTemplateId());

        return savedTemplate;
    }

    /**
     * Activate template.
     */
    @Transactional
    public NotificationTemplate activateTemplate(Long id) {
        log.info("Activating template: {}", id);

        NotificationTemplate template = getTemplateById(id);
        template.setIsActive(true);

        NotificationTemplate savedTemplate = templateRepository.save(template);

        log.info("Template activated successfully: id={}, templateId={}", 
                savedTemplate.getId(), savedTemplate.getTemplateId());

        return savedTemplate;
    }

    /**
     * Deactivate template.
     */
    @Transactional
    public NotificationTemplate deactivateTemplate(Long id) {
        log.info("Deactivating template: {}", id);

        NotificationTemplate template = getTemplateById(id);
        template.setIsActive(false);

        NotificationTemplate savedTemplate = templateRepository.save(template);

        log.info("Template deactivated successfully: id={}, templateId={}", 
                savedTemplate.getId(), savedTemplate.getTemplateId());

        return savedTemplate;
    }

    /**
     * Delete template.
     */
    @Transactional
    public void deleteTemplate(Long id) {
        log.info("Deleting template: {}", id);

        NotificationTemplate template = getTemplateById(id);
        templateRepository.delete(template);

        log.info("Template deleted successfully: id={}, templateId={}", 
                id, template.getTemplateId());
    }

    // =============================================================================
    // Template Response Building
    // =============================================================================

    /**
     * Build template response DTO.
     */
    public NotificationTemplateResponse buildTemplateResponse(NotificationTemplate template) {
        return NotificationTemplateResponse.builder()
                .id(template.getId())
                .templateId(template.getTemplateId())
                .name(template.getName())
                .description(template.getDescription())
                .notificationType(template.getNotificationType())
                .channel(template.getChannel())
                .subjectTemplate(template.getSubjectTemplate())
                .contentTemplate(template.getContentTemplate())
                .htmlTemplate(template.getHtmlTemplate())
                .variables(template.getVariables())
                .isActive(template.getIsActive())
                .version(template.getVersion())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .createdBy(template.getCreatedBy())
                .subjectPreview(truncate(template.getSubjectTemplate(), 100))
                .contentPreview(truncate(template.getContentTemplate(), 200))
                .htmlPreview(truncate(template.getHtmlTemplate(), 200))
                .build();
    }

    // =============================================================================
    // Validation Methods
    // =============================================================================

    /**
     * Validate template.
     */
    private void validateTemplate(NotificationTemplate template) {
        if (template.getTemplateId() == null || template.getTemplateId().trim().isEmpty()) {
            throw new IllegalArgumentException("Template ID is required");
        }

        if (template.getName() == null || template.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Template name is required");
        }

        if (template.getNotificationType() == null) {
            throw new IllegalArgumentException("Notification type is required");
        }

        if (template.getChannel() == null) {
            throw new IllegalArgumentException("Channel is required");
        }

        if (!template.hasSubject() && !template.hasTextContent() && !template.hasHtmlContent()) {
            throw new IllegalArgumentException("Template must have at least one content type");
        }
    }

    // =============================================================================
    // Helper Methods
    // =============================================================================

    /**
     * Truncate string to specified length.
     */
    private String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }

    // =============================================================================
    // Inner Classes
    // =============================================================================

    /**
     * Result of template processing.
     */
    public static class TemplateProcessingResult {
        private final String subject;
        private final String content;

        private TemplateProcessingResult(String subject, String content) {
            this.subject = subject;
            this.content = content;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getSubject() {
            return subject;
        }

        public String getContent() {
            return content;
        }

        public static class Builder {
            private String subject;
            private String content;

            public Builder subject(String subject) {
                this.subject = subject;
                return this;
            }

            public Builder content(String content) {
                this.content = content;
                return this;
            }

            public TemplateProcessingResult build() {
                return new TemplateProcessingResult(subject, content);
            }
        }
    }
}
