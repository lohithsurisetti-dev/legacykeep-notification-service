package com.legacykeep.notification.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * User Notification Preferences Entity
 * 
 * Stores user preferences for notification channels and settings.
 * Controls which notifications a user receives and when.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Entity
@Table(name = "user_notification_preferences", indexes = {
    @Index(name = "idx_user_notification_preferences_user_id", columnList = "user_id")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserNotificationPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "email_enabled", nullable = false)
    private Boolean emailEnabled = true;

    @Column(name = "push_enabled", nullable = false)
    private Boolean pushEnabled = true;

    @Column(name = "sms_enabled", nullable = false)
    private Boolean smsEnabled = false;

    @Column(name = "in_app_enabled", nullable = false)
    private Boolean inAppEnabled = true;

    @Column(name = "marketing_emails_enabled", nullable = false)
    private Boolean marketingEmailsEnabled = false;

    @Column(name = "daily_digest_enabled", nullable = false)
    private Boolean dailyDigestEnabled = true;

    @Column(name = "quiet_hours_enabled", nullable = false)
    private Boolean quietHoursEnabled = false;

    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart = LocalTime.of(22, 0); // 10:00 PM

    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd = LocalTime.of(8, 0); // 8:00 AM

    @Column(name = "timezone", length = 50)
    private String timezone = "UTC";

    @Column(name = "language", length = 10)
    private String language = "en";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // =============================================================================
    // Business Logic Methods
    // =============================================================================

    /**
     * Check if a specific channel is enabled
     */
    public boolean isChannelEnabled(NotificationChannel channel) {
        switch (channel) {
            case EMAIL:
                return emailEnabled;
            case PUSH:
                return pushEnabled;
            case SMS:
                return smsEnabled;
            case IN_APP:
                return inAppEnabled;
            default:
                return false;
        }
    }

    /**
     * Check if notifications should be sent based on quiet hours
     */
    public boolean isWithinQuietHours(LocalTime currentTime) {
        if (!quietHoursEnabled) {
            return false;
        }

        // Handle quiet hours that span midnight
        if (quietHoursStart.isAfter(quietHoursEnd)) {
            // Quiet hours span midnight (e.g., 10 PM to 8 AM)
            return currentTime.isAfter(quietHoursStart) || currentTime.isBefore(quietHoursEnd);
        } else {
            // Quiet hours within same day (e.g., 10 PM to 8 AM next day)
            return currentTime.isAfter(quietHoursStart) && currentTime.isBefore(quietHoursEnd);
        }
    }

    /**
     * Check if marketing emails are allowed
     */
    public boolean isMarketingEmailsAllowed() {
        return emailEnabled && marketingEmailsEnabled;
    }

    /**
     * Check if daily digest is allowed
     */
    public boolean isDailyDigestAllowed() {
        return emailEnabled && dailyDigestEnabled;
    }

    /**
     * Enable a specific channel
     */
    public void enableChannel(NotificationChannel channel) {
        switch (channel) {
            case EMAIL:
                this.emailEnabled = true;
                break;
            case PUSH:
                this.pushEnabled = true;
                break;
            case SMS:
                this.smsEnabled = true;
                break;
            case IN_APP:
                this.inAppEnabled = true;
                break;
        }
    }

    /**
     * Disable a specific channel
     */
    public void disableChannel(NotificationChannel channel) {
        switch (channel) {
            case EMAIL:
                this.emailEnabled = false;
                break;
            case PUSH:
                this.pushEnabled = false;
                break;
            case SMS:
                this.smsEnabled = false;
                break;
            case IN_APP:
                this.inAppEnabled = false;
                break;
        }
    }

    /**
     * Set quiet hours
     */
    public void setQuietHours(LocalTime start, LocalTime end) {
        this.quietHoursStart = start;
        this.quietHoursEnd = end;
        this.quietHoursEnabled = true;
    }

    /**
     * Disable quiet hours
     */
    public void disableQuietHours() {
        this.quietHoursEnabled = false;
    }

    // =============================================================================
    // Constructors
    // =============================================================================

    public UserNotificationPreferences(Long userId) {
        this.userId = userId;
        this.emailEnabled = true;
        this.pushEnabled = true;
        this.smsEnabled = false;
        this.inAppEnabled = true;
        this.marketingEmailsEnabled = false;
        this.dailyDigestEnabled = true;
        this.quietHoursEnabled = false;
        this.quietHoursStart = LocalTime.of(22, 0);
        this.quietHoursEnd = LocalTime.of(8, 0);
        this.timezone = "UTC";
        this.language = "en";
    }
}
