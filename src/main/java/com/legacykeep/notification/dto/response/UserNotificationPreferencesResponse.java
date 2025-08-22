package com.legacykeep.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Response DTO for user notification preferences
 * 
 * This DTO provides a clean representation of user notification preferences
 * for API responses, including all preference settings.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationPreferencesResponse {

    // =============================================================================
    // Core Information
    // =============================================================================

    /**
     * Unique preference ID
     */
    private Long id;

    /**
     * User ID these preferences belong to
     */
    private Long userId;

    // =============================================================================
    // Channel Preferences
    // =============================================================================

    /**
     * Whether email notifications are enabled
     */
    private Boolean emailEnabled;

    /**
     * Whether push notifications are enabled
     */
    private Boolean pushEnabled;

    /**
     * Whether SMS notifications are enabled
     */
    private Boolean smsEnabled;

    /**
     * Whether in-app notifications are enabled
     */
    private Boolean inAppEnabled;

    // =============================================================================
    // Email Preferences
    // =============================================================================

    /**
     * Whether marketing emails are enabled
     */
    private Boolean marketingEmailsEnabled;

    /**
     * Whether daily digest emails are enabled
     */
    private Boolean dailyDigestEnabled;

    // =============================================================================
    // Quiet Hours Settings
    // =============================================================================

    /**
     * Whether quiet hours are enabled
     */
    private Boolean quietHoursEnabled;

    /**
     * Quiet hours start time
     */
    @JsonFormat(pattern = "HH:mm")
    private LocalTime quietHoursStart;

    /**
     * Quiet hours end time
     */
    @JsonFormat(pattern = "HH:mm")
    private LocalTime quietHoursEnd;

    // =============================================================================
    // Localization Settings
    // =============================================================================

    /**
     * User's timezone preference
     */
    private String timezone;

    /**
     * User's language preference
     */
    private String language;

    // =============================================================================
    // Timestamps
    // =============================================================================

    /**
     * When preferences were created
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    /**
     * When preferences were last updated
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    // =============================================================================
    // Utility Methods
    // =============================================================================

    /**
     * Check if user has any notifications enabled
     */
    public boolean hasAnyNotificationsEnabled() {
        return emailEnabled || pushEnabled || smsEnabled || inAppEnabled;
    }

    /**
     * Check if user has all notifications disabled
     */
    public boolean hasAllNotificationsDisabled() {
        return !emailEnabled && !pushEnabled && !smsEnabled && !inAppEnabled;
    }

    /**
     * Get count of enabled notification channels
     */
    public int getEnabledChannelCount() {
        int count = 0;
        if (emailEnabled) count++;
        if (pushEnabled) count++;
        if (smsEnabled) count++;
        if (inAppEnabled) count++;
        return count;
    }

    /**
     * Check if a specific channel is enabled
     */
    public boolean isChannelEnabled(String channel) {
        return switch (channel.toUpperCase()) {
            case "EMAIL" -> emailEnabled;
            case "PUSH" -> pushEnabled;
            case "SMS" -> smsEnabled;
            case "IN_APP" -> inAppEnabled;
            default -> false;
        };
    }

    /**
     * Check if marketing emails are allowed (email enabled AND marketing enabled)
     */
    public boolean isMarketingEmailsAllowed() {
        return emailEnabled && marketingEmailsEnabled;
    }

    /**
     * Check if daily digest is allowed (email enabled AND digest enabled)
     */
    public boolean isDailyDigestAllowed() {
        return emailEnabled && dailyDigestEnabled;
    }

    /**
     * Check if quiet hours are currently active
     */
    public boolean isInQuietHours() {
        if (!quietHoursEnabled || quietHoursStart == null || quietHoursEnd == null) {
            return false;
        }

        LocalTime now = LocalTime.now();
        
        // Handle quiet hours that span midnight
        if (quietHoursStart.isAfter(quietHoursEnd)) {
            // Quiet hours span midnight (e.g., 10 PM to 8 AM)
            return now.isAfter(quietHoursStart) || now.isBefore(quietHoursEnd);
        } else {
            // Quiet hours within same day
            return now.isAfter(quietHoursStart) && now.isBefore(quietHoursEnd);
        }
    }

    /**
     * Get quiet hours duration in minutes
     */
    public Integer getQuietHoursDurationMinutes() {
        if (!quietHoursEnabled || quietHoursStart == null || quietHoursEnd == null) {
            return null;
        }

        if (quietHoursStart.isAfter(quietHoursEnd)) {
            // Span midnight: calculate as (24h - start) + end
            return (24 * 60 - quietHoursStart.getHour() * 60 - quietHoursStart.getMinute()) +
                   (quietHoursEnd.getHour() * 60 + quietHoursEnd.getMinute());
        } else {
            // Same day: simple difference
            return (quietHoursEnd.getHour() * 60 + quietHoursEnd.getMinute()) -
                   (quietHoursStart.getHour() * 60 + quietHoursStart.getMinute());
        }
    }
}
