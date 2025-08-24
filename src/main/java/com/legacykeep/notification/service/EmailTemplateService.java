package com.legacykeep.notification.service;

import com.legacykeep.notification.event.dto.UserEmailVerifiedEvent;
import com.legacykeep.notification.event.dto.UserPasswordResetRequestedEvent;
import com.legacykeep.notification.event.dto.UserRegisteredEvent;
import com.legacykeep.notification.event.dto.UserEmailVerificationRequestedEvent;

/**
 * Service for sending templated emails using Thymeleaf.
 * 
 * Handles email template processing and delivery for various user events.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface EmailTemplateService {

    /**
     * Send welcome email to newly registered user.
     * 
     * @param event User registration event containing user details
     */
    void sendWelcomeEmail(UserRegisteredEvent event);

    /**
     * Send welcome email to user after email verification.
     * 
     * @param event User email verification event containing user details
     */
    void sendWelcomeEmailAfterVerification(UserEmailVerifiedEvent event);

    /**
     * Send email verification email to user.
     * 
     * @param event User email verification requested event containing user details
     */
    void sendEmailVerificationEmail(UserEmailVerificationRequestedEvent event);

    /**
     * Send email verification confirmation.
     * 
     * @param event User email verification event
     */
    void sendEmailVerificationConfirmation(UserEmailVerifiedEvent event);

    /**
     * Send password reset email.
     * 
     * @param event User password reset request event
     */
    void sendPasswordResetEmail(UserPasswordResetRequestedEvent event);
}
