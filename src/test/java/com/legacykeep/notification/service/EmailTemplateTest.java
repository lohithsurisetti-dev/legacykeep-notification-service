package com.legacykeep.notification.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple Email Template Test
 * 
 * Tests email template processing without requiring full Spring context.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.mail.host=localhost",
    "spring.mail.port=3025",
    "spring.mail.username=test@legacykeep.com",
    "spring.mail.password=test-password",
    "spring.mail.properties.mail.smtp.auth=false",
    "spring.mail.properties.mail.smtp.starttls.enable=false",
    "spring.main.allow-bean-definition-overriding=true"
})
class EmailTemplateTest {

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void testEmailVerificationTemplateProcessing() {
        // Test email verification template processing
        Context context = new Context();
        context.setVariable("userName", "Test User");
        context.setVariable("verificationUrl", "https://legacykeep.com/verify?token=test-token-123");
        context.setVariable("expiryHours", 24);
        context.setVariable("logoUrl", "https://legacykeep.com/logo.png");
        context.setVariable("privacyUrl", "https://legacykeep.com/privacy");
        context.setVariable("helpUrl", "https://legacykeep.com/help");

        String htmlContent = templateEngine.process("email/auth/email-verification", context);
        
        assertNotNull(htmlContent, "Template should be processed");
        assertTrue(htmlContent.contains("Test User"), "Template should contain user name");
        assertTrue(htmlContent.contains("test-token-123"), "Template should contain verification URL");
        assertTrue(htmlContent.contains("24"), "Template should contain expiry hours");
        assertTrue(htmlContent.contains("Verify Email Address"), "Template should contain CTA button");
    }

    @Test
    void testPasswordResetTemplateProcessing() {
        // Test password reset template processing
        Context context = new Context();
        context.setVariable("userName", "Test User");
        context.setVariable("resetUrl", "https://legacykeep.com/reset-password?token=test-reset-token-456");
        context.setVariable("expiryHours", 1);
        context.setVariable("logoUrl", "https://legacykeep.com/logo.png");
        context.setVariable("privacyUrl", "https://legacykeep.com/privacy");
        context.setVariable("helpUrl", "https://legacykeep.com/help");

        String htmlContent = templateEngine.process("email/auth/password-reset", context);
        
        assertNotNull(htmlContent, "Template should be processed");
        assertTrue(htmlContent.contains("Test User"), "Template should contain user name");
        assertTrue(htmlContent.contains("test-reset-token-456"), "Template should contain reset URL");
        assertTrue(htmlContent.contains("1"), "Template should contain expiry hours");
        assertTrue(htmlContent.contains("Reset Password"), "Template should contain CTA button");
        assertTrue(htmlContent.contains("Security Notice"), "Template should contain security warning");
    }

    @Test
    void testTemplateEngineAvailability() {
        assertNotNull(templateEngine, "Template engine should be available");
    }
}


















