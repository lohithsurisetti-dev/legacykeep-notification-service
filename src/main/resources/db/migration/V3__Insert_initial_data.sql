-- =============================================================================
-- LegacyKeep Notification Service - Database Migration V3
-- Insert initial data and sample templates
-- =============================================================================

-- =============================================================================
-- Sample Email Templates for Authentication
-- =============================================================================

-- Email verification template
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
    created_by
) VALUES (
    'email-verification',
    'Email Verification',
    'Template for email verification during user registration',
    'EMAIL',
    'EMAIL',
    'Verify your LegacyKeep account - {{userName}}',
    'Hi {{userName}},

Welcome to LegacyKeep! Please verify your email address by clicking the link below:

{{verificationUrl}}

This link will expire in {{expiryHours}} hours.

If you didn''t create this account, you can safely ignore this email.

Best regards,
The LegacyKeep Team',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verify your LegacyKeep account</title>
</head>
<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
    <div style="text-align: center; margin-bottom: 30px;">
        <h1 style="color: #2c3e50; margin-bottom: 10px;">Welcome to LegacyKeep!</h1>
        <p style="color: #7f8c8d; font-size: 18px;">Hi {{userName}},</p>
    </div>
    
    <div style="background-color: #f8f9fa; padding: 30px; border-radius: 8px; margin-bottom: 30px;">
        <p style="font-size: 16px; margin-bottom: 20px;">
            Thank you for joining LegacyKeep! To complete your registration, please verify your email address by clicking the button below:
        </p>
        
        <div style="text-align: center; margin: 30px 0;">
            <a href="{{verificationUrl}}" style="background-color: #3498db; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                Verify Email Address
            </a>
        </div>
        
        <p style="font-size: 14px; color: #7f8c8d; margin-bottom: 20px;">
            This verification link will expire in <strong>{{expiryHours}} hours</strong>.
        </p>
        
        <p style="font-size: 14px; color: #7f8c8d;">
            If the button doesn''t work, you can copy and paste this link into your browser:<br>
            <a href="{{verificationUrl}}" style="color: #3498db;">{{verificationUrl}}</a>
        </p>
    </div>
    
    <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #ecf0f1;">
        <p style="font-size: 14px; color: #7f8c8d;">
            If you didn''t create this account, you can safely ignore this email.
        </p>
        <p style="font-size: 14px; color: #7f8c8d; margin-top: 10px;">
            Best regards,<br>
            <strong>The LegacyKeep Team</strong>
        </p>
    </div>
</body>
</html>',
    '{"userName": "string", "verificationUrl": "string", "expiryHours": "number"}',
    true,
    '1.0',
    'system'
);

-- Password reset template
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
    created_by
) VALUES (
    'password-reset',
    'Password Reset',
    'Template for password reset requests',
    'EMAIL',
    'EMAIL',
    'Reset your LegacyKeep password - {{userName}}',
    'Hi {{userName}},

You requested to reset your LegacyKeep password. Click the link below to create a new password:

{{resetUrl}}

This link will expire in {{expiryHours}} hours.

If you didn''t request this password reset, you can safely ignore this email.

Best regards,
The LegacyKeep Team',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset your LegacyKeep password</title>
</head>
<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
    <div style="text-align: center; margin-bottom: 30px;">
        <h1 style="color: #e74c3c; margin-bottom: 10px;">Password Reset Request</h1>
        <p style="color: #7f8c8d; font-size: 18px;">Hi {{userName}},</p>
    </div>
    
    <div style="background-color: #f8f9fa; padding: 30px; border-radius: 8px; margin-bottom: 30px;">
        <p style="font-size: 16px; margin-bottom: 20px;">
            We received a request to reset your LegacyKeep password. Click the button below to create a new password:
        </p>
        
        <div style="text-align: center; margin: 30px 0;">
            <a href="{{resetUrl}}" style="background-color: #e74c3c; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                Reset Password
            </a>
        </div>
        
        <p style="font-size: 14px; color: #7f8c8d; margin-bottom: 20px;">
            This reset link will expire in <strong>{{expiryHours}} hours</strong>.
        </p>
        
        <p style="font-size: 14px; color: #7f8c8d;">
            If the button doesn''t work, you can copy and paste this link into your browser:<br>
            <a href="{{resetUrl}}" style="color: #e74c3c;">{{resetUrl}}</a>
        </p>
    </div>
    
    <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #ecf0f1;">
        <p style="font-size: 14px; color: #7f8c8d;">
            If you didn''t request this password reset, you can safely ignore this email.
        </p>
        <p style="font-size: 14px; color: #7f8c8d; margin-top: 10px;">
            Best regards,<br>
            <strong>The LegacyKeep Team</strong>
        </p>
    </div>
</body>
</html>',
    '{"userName": "string", "resetUrl": "string", "expiryHours": "number"}',
    true,
    '1.0',
    'system'
);

-- Welcome email template
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
    created_by
) VALUES (
    'welcome-email',
    'Welcome Email',
    'Template for welcome email after email verification',
    'EMAIL',
    'EMAIL',
    'Welcome to LegacyKeep, {{userName}}! Your account is now active',
    'Hi {{userName}},

Welcome to LegacyKeep! Your account has been successfully verified and is now active.

Start preserving your family''s stories and memories today:

{{appUrl}}

If you have any questions or need help getting started, feel free to reach out to our support team.

Best regards,
The LegacyKeep Team',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome to LegacyKeep!</title>
</head>
<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
    <div style="text-align: center; margin-bottom: 30px;">
        <h1 style="color: #27ae60; margin-bottom: 10px;">Welcome to LegacyKeep!</h1>
        <p style="color: #7f8c8d; font-size: 18px;">Hi {{userName}},</p>
    </div>
    
    <div style="background-color: #f8f9fa; padding: 30px; border-radius: 8px; margin-bottom: 30px;">
        <p style="font-size: 16px; margin-bottom: 20px;">
            🎉 Congratulations! Your account has been successfully verified and is now active.
        </p>
        
        <p style="font-size: 16px; margin-bottom: 20px;">
            You''re now ready to start preserving your family''s precious stories, memories, and traditions for generations to come.
        </p>
        
        <div style="text-align: center; margin: 30px 0;">
            <a href="{{appUrl}}" style="background-color: #27ae60; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                Get Started
            </a>
        </div>
        
        <div style="background-color: white; padding: 20px; border-radius: 5px; margin: 20px 0;">
            <h3 style="color: #2c3e50; margin-bottom: 15px;">What you can do now:</h3>
            <ul style="text-align: left; color: #7f8c8d;">
                <li>📖 Create your first family story</li>
                <li>👨‍👩‍👧‍👦 Invite family members to join</li>
                <li>📸 Upload photos and videos</li>
                <li>🎙️ Record voice messages</li>
                <li>📅 Set up family events</li>
            </ul>
        </div>
    </div>
    
    <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #ecf0f1;">
        <p style="font-size: 14px; color: #7f8c8d;">
            If you have any questions or need help getting started, feel free to reach out to our support team.
        </p>
        <p style="font-size: 14px; color: #7f8c8d; margin-top: 10px;">
            Best regards,<br>
            <strong>The LegacyKeep Team</strong>
        </p>
    </div>
</body>
</html>',
    '{"userName": "string", "appUrl": "string"}',
    true,
    '1.0',
    'system'
);

-- Account locked template
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
    created_by
) VALUES (
    'account-locked',
    'Account Locked',
    'Template for account lockout notifications',
    'EMAIL',
    'EMAIL',
    'Your LegacyKeep account has been temporarily locked - {{userName}}',
    'Hi {{userName}},

Your LegacyKeep account has been temporarily locked due to multiple failed login attempts.

To unlock your account, please click the link below:

{{unlockUrl}}

This link will expire in {{expiryHours}} hours.

If you didn''t attempt to log in, please contact our support team immediately.

Best regards,
The LegacyKeep Team',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Locked - LegacyKeep</title>
</head>
<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
    <div style="text-align: center; margin-bottom: 30px;">
        <h1 style="color: #f39c12; margin-bottom: 10px;">Account Temporarily Locked</h1>
        <p style="color: #7f8c8d; font-size: 18px;">Hi {{userName}},</p>
    </div>
    
    <div style="background-color: #f8f9fa; padding: 30px; border-radius: 8px; margin-bottom: 30px;">
        <p style="font-size: 16px; margin-bottom: 20px;">
            🔒 Your LegacyKeep account has been temporarily locked due to multiple failed login attempts.
        </p>
        
        <p style="font-size: 16px; margin-bottom: 20px;">
            This is a security measure to protect your account. You can unlock it by clicking the button below:
        </p>
        
        <div style="text-align: center; margin: 30px 0;">
            <a href="{{unlockUrl}}" style="background-color: #f39c12; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                Unlock Account
            </a>
        </div>
        
        <p style="font-size: 14px; color: #7f8c8d; margin-bottom: 20px;">
            This unlock link will expire in <strong>{{expiryHours}} hours</strong>.
        </p>
        
        <p style="font-size: 14px; color: #7f8c8d;">
            If the button doesn''t work, you can copy and paste this link into your browser:<br>
            <a href="{{unlockUrl}}" style="color: #f39c12;">{{unlockUrl}}</a>
        </p>
    </div>
    
    <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #ecf0f1;">
        <p style="font-size: 14px; color: #7f8c8d;">
            ⚠️ If you didn''t attempt to log in to your account, please contact our support team immediately.
        </p>
        <p style="font-size: 14px; color: #7f8c8d; margin-top: 10px;">
            Best regards,<br>
            <strong>The LegacyKeep Team</strong>
        </p>
    </div>
</body>
</html>',
    '{"userName": "string", "unlockUrl": "string", "expiryHours": "number"}',
    true,
    '1.0',
    'system'
);

-- =============================================================================
-- Sample Push Notification Templates
-- =============================================================================

-- Family invitation push notification
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
    created_by
) VALUES (
    'family-invitation-push',
    'Family Invitation Push',
    'Push notification for family invitations',
    'PUSH',
    'PUSH',
    'Family Invitation',
    '{{inviterName}} invited you to join their family on LegacyKeep',
    null,
    '{"inviterName": "string", "familyName": "string"}',
    true,
    '1.0',
    'system'
);

-- Story shared push notification
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
    created_by
) VALUES (
    'story-shared-push',
    'Story Shared Push',
    'Push notification when a story is shared',
    'PUSH',
    'PUSH',
    'New Family Story',
    '{{authorName}} shared a new story: "{{storyTitle}}"',
    null,
    '{"authorName": "string", "storyTitle": "string"}',
    true,
    '1.0',
    'system'
);

-- =============================================================================
-- Sample SMS Templates
-- =============================================================================

-- SMS verification template
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
    created_by
) VALUES (
    'sms-verification',
    'SMS Verification',
    'SMS template for phone verification',
    'SMS',
    'SMS',
    null,
    'Your LegacyKeep verification code is: {{verificationCode}}. Valid for {{expiryMinutes}} minutes.',
    null,
    '{"verificationCode": "string", "expiryMinutes": "number"}',
    true,
    '1.0',
    'system'
);

-- =============================================================================
-- Comments for Documentation
-- =============================================================================

COMMENT ON TABLE notification_templates IS 'Sample templates inserted for authentication flows and common notifications';
