-- Insert email templates for notification service
INSERT INTO notification_templates (
    template_id, 
    name, 
    description, 
    notification_type, 
    channel, 
    subject_template, 
    content_template, 
    html_template, 
    is_active, 
    version, 
    created_by, 
    created_at, 
    updated_at
) VALUES 
(
    'email-verification',
    'Email Verification Template',
    'Template for email verification during user registration',
    'EMAIL',
    'EMAIL',
    'Verify Your Email - LegacyKeep',
    'Hello {{userName}}, please verify your email by clicking the link below. This link will expire in {{expiryHours}} hours.',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Verification</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background: #4CAF50; color: white; padding: 20px; text-align: center; }
        .content { padding: 20px; background: #f9f9f9; }
        .button { display: inline-block; padding: 12px 24px; background: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>LegacyKeep</h1>
            <h2>Email Verification</h2>
        </div>
        <div class="content">
            <p>Hello <strong>{{userName}}</strong>,</p>
            <p>Thank you for registering with LegacyKeep! To complete your registration, please verify your email address by clicking the button below:</p>
            <p style="text-align: center;">
                <a href="{{verificationUrl}}" class="button">Verify Email Address</a>
            </p>
            <p>This verification link will expire in <strong>{{expiryHours}} hours</strong>.</p>
            <p>If you did not create an account with LegacyKeep, please ignore this email.</p>
        </div>
        <div class="footer">
            <p>&copy; 2025 LegacyKeep. All rights reserved.</p>
        </div>
    </div>
</body>
</html>',
    true,
    '1.0',
    'system',
    NOW(),
    NOW()
),
(
    'password-reset',
    'Password Reset Template',
    'Template for password reset emails',
    'EMAIL',
    'EMAIL',
    'Reset Your Password - LegacyKeep',
    'Hello {{userName}}, you requested a password reset. Click the link below to reset your password. This link will expire in {{expiryHours}} hours.',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Password Reset</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background: #2196F3; color: white; padding: 20px; text-align: center; }
        .content { padding: 20px; background: #f9f9f9; }
        .button { display: inline-block; padding: 12px 24px; background: #2196F3; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
        .warning { background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>LegacyKeep</h1>
            <h2>Password Reset Request</h2>
        </div>
        <div class="content">
            <p>Hello <strong>{{userName}}</strong>,</p>
            <p>We received a request to reset your password for your LegacyKeep account.</p>
            <p style="text-align: center;">
                <a href="{{resetUrl}}" class="button">Reset Password</a>
            </p>
            <p>This password reset link will expire in <strong>{{expiryHours}} hours</strong>.</p>
            <div class="warning">
                <strong>Security Notice:</strong> If you did not request this password reset, please ignore this email and ensure your account is secure.
            </div>
        </div>
        <div class="footer">
            <p>&copy; 2025 LegacyKeep. All rights reserved.</p>
        </div>
    </div>
</body>
</html>',
    true,
    '1.0',
    'system',
    NOW(),
    NOW()
),
(
    'welcome',
    'Welcome Email Template',
    'Template for welcome emails to new users',
    'EMAIL',
    'EMAIL',
    'Welcome to LegacyKeep!',
    'Hello {{userName}}, welcome to LegacyKeep! We''re excited to have you on board.',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome to LegacyKeep</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background: #9C27B0; color: white; padding: 20px; text-align: center; }
        .content { padding: 20px; background: #f9f9f9; }
        .button { display: inline-block; padding: 12px 24px; background: #9C27B0; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>LegacyKeep</h1>
            <h2>Welcome!</h2>
        </div>
        <div class="content">
            <p>Hello <strong>{{userName}}</strong>,</p>
            <p>{{welcomeMessage}}</p>
            <p>We''re thrilled to have you join our community of families preserving their precious memories and stories.</p>
            <p style="text-align: center;">
                <a href="{{loginUrl}}" class="button">Get Started</a>
            </p>
            <p>Here''s what you can do with LegacyKeep:</p>
            <ul>
                <li>Create and share family stories</li>
                <li>Upload and organize photos and videos</li>
                <li>Connect with family members</li>
                <li>Preserve memories for future generations</li>
            </ul>
        </div>
        <div class="footer">
            <p>&copy; 2025 LegacyKeep. All rights reserved.</p>
        </div>
    </div>
</body>
</html>',
    true,
    '1.0',
    'system',
    NOW(),
    NOW()
);
