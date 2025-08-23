package com.legacykeep.notification.exception;

/**
 * Exception thrown when a notification template is not found.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class TemplateNotFoundException extends NotificationException {

    private final String templateId;

    /**
     * Constructor with template ID.
     * 
     * @param templateId The template ID that was not found
     */
    public TemplateNotFoundException(String templateId) {
        super(String.format("Template not found: %s", templateId), 
              "TEMPLATE_NOT_FOUND", "TEMPLATE");
        this.templateId = templateId;
    }

    /**
     * Constructor with template ID and cause.
     * 
     * @param templateId The template ID that was not found
     * @param cause Root cause
     */
    public TemplateNotFoundException(String templateId, Throwable cause) {
        super(String.format("Template not found: %s", templateId), 
              "TEMPLATE_NOT_FOUND", "TEMPLATE", cause);
        this.templateId = templateId;
    }

    /**
     * Get the template ID that was not found.
     * 
     * @return Template ID
     */
    public String getTemplateId() {
        return templateId;
    }
}
