-- =============================================================================
-- LegacyKeep Notification Service - Database Migration V2
-- Create additional tables for rate limiting, analytics, and enhanced functionality
-- =============================================================================

-- =============================================================================
-- Rate Limiting Tables
-- =============================================================================

-- Rate limiting table for notification throttling
CREATE TABLE notification_rate_limits (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL, -- user_id or email
    notification_type notification_type NOT NULL,
    channel notification_channel NOT NULL,
    attempts INTEGER DEFAULT 1,
    window_start TIMESTAMP NOT NULL,
    window_end TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(identifier, notification_type, channel, window_start)
);

-- =============================================================================
-- Analytics Tables
-- =============================================================================

-- Notification analytics table for reporting
CREATE TABLE notification_analytics (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    notification_type notification_type NOT NULL,
    channel notification_channel NOT NULL,
    template_id VARCHAR(100),
    total_sent INTEGER DEFAULT 0,
    total_delivered INTEGER DEFAULT 0,
    total_failed INTEGER DEFAULT 0,
    total_opened INTEGER DEFAULT 0,
    total_clicked INTEGER DEFAULT 0,
    avg_delivery_time_ms INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(date, notification_type, channel, template_id)
);

-- User notification activity table
CREATE TABLE user_notification_activity (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_id BIGINT NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    activity_type VARCHAR(50) NOT NULL, -- 'sent', 'delivered', 'opened', 'clicked', 'failed'
    activity_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- Template Management Tables
-- =============================================================================

-- Template versions table for A/B testing
CREATE TABLE template_versions (
    id BIGSERIAL PRIMARY KEY,
    template_id VARCHAR(100) NOT NULL,
    version VARCHAR(20) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    subject_template TEXT,
    content_template TEXT,
    html_template TEXT,
    variables JSONB,
    is_active BOOLEAN DEFAULT FALSE,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    UNIQUE(template_id, version)
);

-- Template A/B test results table
CREATE TABLE template_ab_test_results (
    id BIGSERIAL PRIMARY KEY,
    test_id VARCHAR(255) NOT NULL,
    template_id VARCHAR(100) NOT NULL,
    version_a VARCHAR(20) NOT NULL,
    version_b VARCHAR(20) NOT NULL,
    winner_version VARCHAR(20),
    total_sent_a INTEGER DEFAULT 0,
    total_sent_b INTEGER DEFAULT 0,
    open_rate_a DECIMAL(5,4),
    open_rate_b DECIMAL(5,4),
    click_rate_a DECIMAL(5,4),
    click_rate_b DECIMAL(5,4),
    conversion_rate_a DECIMAL(5,4),
    conversion_rate_b DECIMAL(5,4),
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'RUNNING', -- 'RUNNING', 'COMPLETED', 'CANCELLED'
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- Device Management Tables
-- =============================================================================

-- User devices table for push notifications
CREATE TABLE user_devices (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_token VARCHAR(500) NOT NULL,
    device_type VARCHAR(20) NOT NULL, -- 'IOS', 'ANDROID', 'WEB'
    device_name VARCHAR(255),
    device_model VARCHAR(255),
    os_version VARCHAR(50),
    app_version VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    last_used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, device_token)
);

-- =============================================================================
-- Queue Management Tables
-- =============================================================================

-- Notification queue table for scheduled notifications
CREATE TABLE notification_queue (
    id BIGSERIAL PRIMARY KEY,
    notification_id BIGINT NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    scheduled_at TIMESTAMP NOT NULL,
    priority notification_priority DEFAULT 'NORMAL',
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    status VARCHAR(20) DEFAULT 'QUEUED', -- 'QUEUED', 'PROCESSING', 'COMPLETED', 'FAILED'
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Dead letter queue table for failed notifications
CREATE TABLE notification_dead_letter_queue (
    id BIGSERIAL PRIMARY KEY,
    notification_id BIGINT NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    failure_reason TEXT NOT NULL,
    failure_count INTEGER DEFAULT 1,
    last_failure_at TIMESTAMP NOT NULL,
    retry_after TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- Additional Indexes for Performance
-- =============================================================================

-- Rate limiting indexes
CREATE INDEX idx_notification_rate_limits_identifier ON notification_rate_limits(identifier);
CREATE INDEX idx_notification_rate_limits_window ON notification_rate_limits(window_start, window_end);
CREATE INDEX idx_notification_rate_limits_type_channel ON notification_rate_limits(notification_type, channel);

-- Analytics indexes
CREATE INDEX idx_notification_analytics_date ON notification_analytics(date);
CREATE INDEX idx_notification_analytics_type_channel ON notification_analytics(notification_type, channel);
CREATE INDEX idx_notification_analytics_template ON notification_analytics(template_id);

-- User activity indexes
CREATE INDEX idx_user_notification_activity_user_id ON user_notification_activity(user_id);
CREATE INDEX idx_user_notification_activity_notification_id ON user_notification_activity(notification_id);
CREATE INDEX idx_user_notification_activity_type ON user_notification_activity(activity_type);
CREATE INDEX idx_user_notification_activity_created_at ON user_notification_activity(created_at);

-- Template version indexes
CREATE INDEX idx_template_versions_template_id ON template_versions(template_id);
CREATE INDEX idx_template_versions_active ON template_versions(is_active);
CREATE INDEX idx_template_versions_default ON template_versions(is_default);

-- A/B test indexes
CREATE INDEX idx_template_ab_test_results_test_id ON template_ab_test_results(test_id);
CREATE INDEX idx_template_ab_test_results_status ON template_ab_test_results(status);
CREATE INDEX idx_template_ab_test_results_date ON template_ab_test_results(start_date, end_date);

-- Device management indexes
CREATE INDEX idx_user_devices_user_id ON user_devices(user_id);
CREATE INDEX idx_user_devices_token ON user_devices(device_token);
CREATE INDEX idx_user_devices_active ON user_devices(is_active);
CREATE INDEX idx_user_devices_type ON user_devices(device_type);

-- Queue management indexes
CREATE INDEX idx_notification_queue_scheduled_at ON notification_queue(scheduled_at);
CREATE INDEX idx_notification_queue_status ON notification_queue(status);
CREATE INDEX idx_notification_queue_priority ON notification_queue(priority);
CREATE INDEX idx_notification_queue_created_at ON notification_queue(created_at);

-- Dead letter queue indexes
CREATE INDEX idx_notification_dead_letter_queue_failure_at ON notification_dead_letter_queue(last_failure_at);
CREATE INDEX idx_notification_dead_letter_queue_retry_after ON notification_dead_letter_queue(retry_after);

-- =============================================================================
-- Additional Comments for Documentation
-- =============================================================================

COMMENT ON TABLE notification_rate_limits IS 'Rate limiting for notification sending to prevent abuse';
COMMENT ON TABLE notification_analytics IS 'Daily analytics for notification performance and reporting';
COMMENT ON TABLE user_notification_activity IS 'User interaction tracking for notifications (opens, clicks, etc.)';
COMMENT ON TABLE template_versions IS 'Version control for notification templates';
COMMENT ON TABLE template_ab_test_results IS 'A/B testing results for template optimization';
COMMENT ON TABLE user_devices IS 'User device information for push notifications';
COMMENT ON TABLE notification_queue IS 'Scheduled notification queue for delayed sending';
COMMENT ON TABLE notification_dead_letter_queue IS 'Failed notifications that cannot be processed';
