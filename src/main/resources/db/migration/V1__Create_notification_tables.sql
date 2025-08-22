-- =============================================================================
-- LegacyKeep Notification Service - Database Migration V1
-- Create core notification tables
-- =============================================================================

-- Create notification_types enum
CREATE TYPE notification_type AS ENUM (
    'EMAIL',
    'PUSH',
    'SMS',
    'IN_APP'
);

-- Create notification_status enum
CREATE TYPE notification_status AS ENUM (
    'PENDING',
    'PROCESSING',
    'SENT',
    'DELIVERED',
    'FAILED',
    'CANCELLED'
);

-- Create notification_priority enum
CREATE TYPE notification_priority AS ENUM (
    'LOW',
    'NORMAL',
    'HIGH',
    'URGENT'
);

-- Create notification_channel enum
CREATE TYPE notification_channel AS ENUM (
    'EMAIL',
    'PUSH',
    'SMS',
    'IN_APP'
);

-- =============================================================================
-- Core Notification Tables
-- =============================================================================

-- Notifications table - Main notification entity
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(255) UNIQUE NOT NULL,
    notification_type notification_type NOT NULL,
    template_id VARCHAR(100) NOT NULL,
    recipient_id BIGINT NOT NULL,
    recipient_email VARCHAR(255),
    recipient_phone VARCHAR(20),
    recipient_device_token VARCHAR(500),
    subject VARCHAR(255),
    content TEXT,
    template_data JSONB,
    priority notification_priority DEFAULT 'NORMAL',
    status notification_status DEFAULT 'PENDING',
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    metadata JSONB
);

-- Notification templates table
CREATE TABLE notification_templates (
    id BIGSERIAL PRIMARY KEY,
    template_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    notification_type notification_type NOT NULL,
    channel notification_channel NOT NULL,
    subject_template TEXT,
    content_template TEXT,
    html_template TEXT,
    variables JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    version VARCHAR(20) DEFAULT '1.0',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100)
);

-- Notification deliveries table - Track delivery status
CREATE TABLE notification_deliveries (
    id BIGSERIAL PRIMARY KEY,
    notification_id BIGINT NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    channel notification_channel NOT NULL,
    status notification_status NOT NULL,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,
    delivery_metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- User Preferences Tables
-- =============================================================================

-- User notification preferences table
CREATE TABLE user_notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    email_enabled BOOLEAN DEFAULT TRUE,
    push_enabled BOOLEAN DEFAULT TRUE,
    sms_enabled BOOLEAN DEFAULT FALSE,
    in_app_enabled BOOLEAN DEFAULT TRUE,
    marketing_emails_enabled BOOLEAN DEFAULT FALSE,
    daily_digest_enabled BOOLEAN DEFAULT TRUE,
    quiet_hours_enabled BOOLEAN DEFAULT FALSE,
    quiet_hours_start TIME DEFAULT '22:00:00',
    quiet_hours_end TIME DEFAULT '08:00:00',
    timezone VARCHAR(50) DEFAULT 'UTC',
    language VARCHAR(10) DEFAULT 'en',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id)
);

-- =============================================================================
-- Event Tracking Tables
-- =============================================================================

-- Notification events table - Audit log for events
CREATE TABLE notification_events (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(255) UNIQUE NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_version VARCHAR(20) DEFAULT '1.0',
    source_service VARCHAR(100) NOT NULL,
    source_user_id BIGINT,
    correlation_id VARCHAR(255),
    request_id VARCHAR(255),
    event_data JSONB,
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- Indexes for Performance
-- =============================================================================

-- Notifications table indexes
CREATE INDEX idx_notifications_event_id ON notifications(event_id);
CREATE INDEX idx_notifications_recipient_id ON notifications(recipient_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_type ON notifications(notification_type);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_scheduled_at ON notifications(scheduled_at);
CREATE INDEX idx_notifications_template_id ON notifications(template_id);

-- Notification templates table indexes
CREATE INDEX idx_notification_templates_template_id ON notification_templates(template_id);
CREATE INDEX idx_notification_templates_type ON notification_templates(notification_type);
CREATE INDEX idx_notification_templates_active ON notification_templates(is_active);

-- Notification deliveries table indexes
CREATE INDEX idx_notification_deliveries_notification_id ON notification_deliveries(notification_id);
CREATE INDEX idx_notification_deliveries_status ON notification_deliveries(status);
CREATE INDEX idx_notification_deliveries_channel ON notification_deliveries(channel);
CREATE INDEX idx_notification_deliveries_sent_at ON notification_deliveries(sent_at);

-- User notification preferences table indexes
CREATE INDEX idx_user_notification_preferences_user_id ON user_notification_preferences(user_id);

-- Notification events table indexes
CREATE INDEX idx_notification_events_event_id ON notification_events(event_id);
CREATE INDEX idx_notification_events_event_type ON notification_events(event_type);
CREATE INDEX idx_notification_events_source_service ON notification_events(source_service);
CREATE INDEX idx_notification_events_created_at ON notification_events(created_at);
CREATE INDEX idx_notification_events_correlation_id ON notification_events(correlation_id);

-- =============================================================================
-- Comments for Documentation
-- =============================================================================

COMMENT ON TABLE notifications IS 'Main notifications table storing all notification requests';
COMMENT ON TABLE notification_templates IS 'Email and push notification templates';
COMMENT ON TABLE notification_deliveries IS 'Delivery tracking for each notification channel';
COMMENT ON TABLE user_notification_preferences IS 'User preferences for notification channels and settings';
COMMENT ON TABLE notification_events IS 'Audit log of all notification events received from other services';
