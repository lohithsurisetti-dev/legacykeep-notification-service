-- =============================================================================
-- LegacyKeep Notification Service - Database Initialization
-- =============================================================================

-- Create the notification database if it doesn't exist
-- (This will be handled by Docker environment variables)

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Create custom functions for JSON operations
CREATE OR REPLACE FUNCTION jsonb_merge(a jsonb, b jsonb)
RETURNS jsonb AS $$
BEGIN
    RETURN a || b;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create function to generate event IDs
CREATE OR REPLACE FUNCTION generate_event_id()
RETURNS VARCHAR AS $$
BEGIN
    RETURN 'evt_' || to_char(now(), 'YYYYMMDD_HH24MISS') || '_' || substr(md5(random()::text), 1, 8);
END;
$$ LANGUAGE plpgsql;

-- Create function to check if notification should be sent based on user preferences
CREATE OR REPLACE FUNCTION should_send_notification(
    p_user_id BIGINT,
    p_notification_type VARCHAR,
    p_channel VARCHAR
)
RETURNS BOOLEAN AS $$
DECLARE
    user_prefs RECORD;
    current_time TIME;
    current_hour INTEGER;
BEGIN
    -- Get user preferences
    SELECT * INTO user_prefs 
    FROM user_notification_preferences 
    WHERE user_id = p_user_id;
    
    -- If no preferences found, allow by default
    IF NOT FOUND THEN
        RETURN TRUE;
    END IF;
    
    -- Check if channel is enabled
    CASE p_channel
        WHEN 'EMAIL' THEN
            IF NOT user_prefs.email_enabled THEN
                RETURN FALSE;
            END IF;
        WHEN 'PUSH' THEN
            IF NOT user_prefs.push_enabled THEN
                RETURN FALSE;
            END IF;
        WHEN 'SMS' THEN
            IF NOT user_prefs.sms_enabled THEN
                RETURN FALSE;
            END IF;
        WHEN 'IN_APP' THEN
            IF NOT user_prefs.in_app_enabled THEN
                RETURN FALSE;
            END IF;
        ELSE
            RETURN FALSE;
    END CASE;
    
    -- Check quiet hours
    IF user_prefs.quiet_hours_enabled THEN
        current_time := CURRENT_TIME;
        current_hour := EXTRACT(HOUR FROM current_time);
        
        -- Check if current time is within quiet hours
        IF current_hour >= EXTRACT(HOUR FROM user_prefs.quiet_hours_start) OR 
           current_hour < EXTRACT(HOUR FROM user_prefs.quiet_hours_end) THEN
            RETURN FALSE;
        END IF;
    END IF;
    
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- Create function to get notification statistics
CREATE OR REPLACE FUNCTION get_notification_stats(
    p_start_date DATE DEFAULT CURRENT_DATE - INTERVAL '30 days',
    p_end_date DATE DEFAULT CURRENT_DATE
)
RETURNS TABLE (
    notification_type VARCHAR,
    channel VARCHAR,
    total_sent BIGINT,
    total_delivered BIGINT,
    total_failed BIGINT,
    delivery_rate DECIMAL(5,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        n.notification_type::VARCHAR,
        nd.channel::VARCHAR,
        COUNT(*)::BIGINT as total_sent,
        COUNT(CASE WHEN nd.status = 'DELIVERED' THEN 1 END)::BIGINT as total_delivered,
        COUNT(CASE WHEN nd.status = 'FAILED' THEN 1 END)::BIGINT as total_failed,
        ROUND(
            (COUNT(CASE WHEN nd.status = 'DELIVERED' THEN 1 END)::DECIMAL / COUNT(*)::DECIMAL) * 100, 
            2
        ) as delivery_rate
    FROM notifications n
    JOIN notification_deliveries nd ON n.id = nd.notification_id
    WHERE n.created_at::DATE BETWEEN p_start_date AND p_end_date
    GROUP BY n.notification_type, nd.channel
    ORDER BY n.notification_type, nd.channel;
END;
$$ LANGUAGE plpgsql;

-- Create function to clean up old notifications
CREATE OR REPLACE FUNCTION cleanup_old_notifications(
    p_days_to_keep INTEGER DEFAULT 90
)
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM notifications 
    WHERE created_at < CURRENT_DATE - INTERVAL '1 day' * p_days_to_keep
    AND status IN ('DELIVERED', 'FAILED', 'CANCELLED');
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Grant permissions to postgres user (for development)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO postgres;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_notifications_created_at_status ON notifications(created_at, status);
CREATE INDEX IF NOT EXISTS idx_notifications_recipient_type ON notifications(recipient_id, notification_type);
CREATE INDEX IF NOT EXISTS idx_notification_deliveries_created_at ON notification_deliveries(created_at);
CREATE INDEX IF NOT EXISTS idx_notification_events_processed ON notification_events(processed_at);

-- Create partial indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_notifications_pending ON notifications(created_at) 
WHERE status = 'PENDING';

CREATE INDEX IF NOT EXISTS idx_notifications_failed ON notifications(failed_at) 
WHERE status = 'FAILED';

CREATE INDEX IF NOT EXISTS idx_notification_deliveries_failed ON notification_deliveries(failed_at) 
WHERE status = 'FAILED';

-- Create GIN indexes for JSONB columns
CREATE INDEX IF NOT EXISTS idx_notifications_template_data ON notifications USING GIN (template_data);
CREATE INDEX IF NOT EXISTS idx_notifications_metadata ON notifications USING GIN (metadata);
CREATE INDEX IF NOT EXISTS idx_notification_events_event_data ON notification_events USING GIN (event_data);
CREATE INDEX IF NOT EXISTS idx_notification_deliveries_delivery_metadata ON notification_deliveries USING GIN (delivery_metadata);

-- Create triggers for updated_at columns
DO $$
DECLARE
    table_record RECORD;
BEGIN
    FOR table_record IN 
        SELECT table_name 
        FROM information_schema.tables 
        WHERE table_schema = 'public' 
        AND table_name IN (
            'notifications',
            'notification_templates', 
            'notification_deliveries',
            'user_notification_preferences',
            'notification_events',
            'notification_rate_limits',
            'notification_analytics',
            'user_notification_activity',
            'template_versions',
            'template_ab_test_results',
            'user_devices',
            'notification_queue',
            'notification_dead_letter_queue'
        )
    LOOP
        EXECUTE format('
            DROP TRIGGER IF EXISTS update_%I_updated_at ON %I;
            CREATE TRIGGER update_%I_updated_at
                BEFORE UPDATE ON %I
                FOR EACH ROW
                EXECUTE FUNCTION update_updated_at_column();
        ', table_record.table_name, table_record.table_name, 
           table_record.table_name, table_record.table_name);
    END LOOP;
END $$;

-- Insert default notification preferences for system user
INSERT INTO user_notification_preferences (
    user_id,
    email_enabled,
    push_enabled,
    sms_enabled,
    in_app_enabled,
    marketing_emails_enabled,
    daily_digest_enabled,
    quiet_hours_enabled,
    timezone,
    language
) VALUES (
    0, -- System user ID
    true,
    true,
    false,
    true,
    false,
    true,
    false,
    'UTC',
    'en'
) ON CONFLICT (user_id) DO NOTHING;

-- Create a view for notification summary
CREATE OR REPLACE VIEW notification_summary AS
SELECT 
    DATE(created_at) as notification_date,
    notification_type,
    status,
    COUNT(*) as count,
    COUNT(CASE WHEN status = 'DELIVERED' THEN 1 END) as delivered_count,
    COUNT(CASE WHEN status = 'FAILED' THEN 1 END) as failed_count,
    ROUND(
        (COUNT(CASE WHEN status = 'DELIVERED' THEN 1 END)::DECIMAL / COUNT(*)::DECIMAL) * 100, 
        2
    ) as delivery_rate
FROM notifications
WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY DATE(created_at), notification_type, status
ORDER BY notification_date DESC, notification_type, status;

-- Grant permissions on the view
GRANT SELECT ON notification_summary TO postgres;

-- Log the initialization
INSERT INTO notification_events (
    event_id,
    event_type,
    event_version,
    source_service,
    event_data,
    processed_at
) VALUES (
    generate_event_id(),
    'DATABASE_INITIALIZED',
    '1.0',
    'notification-service',
    '{"message": "Database initialized successfully", "timestamp": "' || CURRENT_TIMESTAMP || '"}',
    CURRENT_TIMESTAMP
);
