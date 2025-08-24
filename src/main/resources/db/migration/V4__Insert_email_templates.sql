-- Insert email templates
INSERT INTO notification_templates (
    template_id, 
    name, 
    description, 
    notification_type, 
    channel, 
    subject_template, 
    content_template, 
    html_template, 
    variables, 
    is_active, 
    version, 
    created_by, 
    created_at, 
    updated_at
) VALUES 
(
    'email-verification',
    'Email Verification',
    'Template for email verification during user registration',
    'EMAIL',
    'EMAIL',
    'Verify your LegacyKeep account - {{userName}}',
    'Hello {{userName}}, please verify your email by clicking the link: {{verificationUrl}}',
    'email/auth/email-verification',
    '["userName", "verificationUrl", "expiryHours", "logoUrl", "privacyUrl", "helpUrl"]',
    true,
    1,
    'system',
    NOW(),
    NOW()
),
(
    'password-reset',
    'Password Reset',
    'Template for password reset requests',
    'EMAIL',
    'EMAIL',
    'Reset your LegacyKeep password - {{userName}}',
    'Hello {{userName}}, reset your password by clicking the link: {{resetUrl}}',
    'email/auth/password-reset',
    '["userName", "resetUrl", "expiryHours", "logoUrl", "privacyUrl", "helpUrl"]',
    true,
    1,
    'system',
    NOW(),
    NOW()
),
(
    'welcome',
    'Welcome Email',
    'Template for welcoming new users after email verification',
    'EMAIL',
    'EMAIL',
    'Welcome to LegacyKeep, {{userName}}! 🎉',
    'Welcome {{userName}}! Your account has been verified and you can now start preserving your family memories.',
    'email/auth/welcome',
    '["userName", "dashboardUrl", "helpUrl", "logoUrl"]',
    true,
    1,
    'system',
    NOW(),
    NOW()
);


















