package com.legacykeep.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for notification analytics and reporting
 * 
 * This DTO provides comprehensive analytics data for notifications
 * including delivery rates, performance metrics, and trends.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    // =============================================================================
    // Time Range Information
    // =============================================================================

    /**
     * Start date of the analytics period
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startDate;

    /**
     * End date of the analytics period
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endDate;

    /**
     * Analytics period type (daily, weekly, monthly, custom)
     */
    private String periodType;

    // =============================================================================
    // Overall Statistics
    // =============================================================================

    /**
     * Total notifications sent
     */
    private Long totalNotifications;

    /**
     * Total notifications delivered successfully
     */
    private Long deliveredNotifications;

    /**
     * Total notifications failed
     */
    private Long failedNotifications;

    /**
     * Total notifications pending
     */
    private Long pendingNotifications;

    /**
     * Overall delivery success rate (percentage)
     */
    private Double deliverySuccessRate;

    /**
     * Overall failure rate (percentage)
     */
    private Double failureRate;

    // =============================================================================
    // Channel-specific Statistics
    // =============================================================================

    /**
     * Statistics by notification channel
     */
    private Map<String, ChannelStats> channelStats;

    /**
     * Statistics by notification type
     */
    private Map<String, TypeStats> typeStats;

    /**
     * Statistics by template
     */
    private Map<String, TemplateStats> templateStats;

    // =============================================================================
    // Performance Metrics
    // =============================================================================

    /**
     * Average delivery time in milliseconds
     */
    private Long averageDeliveryTimeMs;

    /**
     * Median delivery time in milliseconds
     */
    private Long medianDeliveryTimeMs;

    /**
     * 95th percentile delivery time in milliseconds
     */
    private Long p95DeliveryTimeMs;

    /**
     * 99th percentile delivery time in milliseconds
     */
    private Long p99DeliveryTimeMs;

    /**
     * Fastest delivery time in milliseconds
     */
    private Long fastestDeliveryTimeMs;

    /**
     * Slowest delivery time in milliseconds
     */
    private Long slowestDeliveryTimeMs;

    // =============================================================================
    // Time-based Trends
    // =============================================================================

    /**
     * Hourly notification volume
     */
    private List<HourlyStats> hourlyStats;

    /**
     * Daily notification volume
     */
    private List<DailyStats> dailyStats;

    /**
     * Weekly notification volume
     */
    private List<WeeklyStats> weeklyStats;

    /**
     * Monthly notification volume
     */
    private List<MonthlyStats> monthlyStats;

    // =============================================================================
    // Error Analysis
    // =============================================================================

    /**
     * Top failure reasons
     */
    private List<FailureReason> topFailureReasons;

    /**
     * Retry statistics
     */
    private RetryStats retryStats;

    // =============================================================================
    // User Engagement
    // =============================================================================

    /**
     * Most active users (by notification count)
     */
    private List<UserActivity> topActiveUsers;

    /**
     * User preference statistics
     */
    private UserPreferenceStats userPreferenceStats;

    // =============================================================================
    // Inner Classes for Detailed Statistics
    // =============================================================================

    /**
     * Statistics for a specific notification channel
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelStats {
        private String channel;
        private Long totalSent;
        private Long delivered;
        private Long failed;
        private Long pending;
        private Double successRate;
        private Long averageDeliveryTimeMs;
        private Long totalRetries;
    }

    /**
     * Statistics for a specific notification type
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeStats {
        private String type;
        private Long totalSent;
        private Long delivered;
        private Long failed;
        private Long pending;
        private Double successRate;
        private Long averageDeliveryTimeMs;
    }

    /**
     * Statistics for a specific template
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateStats {
        private String templateId;
        private String templateName;
        private Long totalSent;
        private Long delivered;
        private Long failed;
        private Long pending;
        private Double successRate;
        private Long averageDeliveryTimeMs;
        private Long totalRetries;
    }

    /**
     * Hourly statistics
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyStats {
        private Integer hour;
        private Long totalSent;
        private Long delivered;
        private Long failed;
        private Double successRate;
    }

    /**
     * Daily statistics
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime date;
        private Long totalSent;
        private Long delivered;
        private Long failed;
        private Double successRate;
    }

    /**
     * Weekly statistics
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyStats {
        private Integer weekOfYear;
        private Integer year;
        private Long totalSent;
        private Long delivered;
        private Long failed;
        private Double successRate;
    }

    /**
     * Monthly statistics
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyStats {
        private Integer month;
        private Integer year;
        private Long totalSent;
        private Long delivered;
        private Long failed;
        private Double successRate;
    }

    /**
     * Failure reason statistics
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailureReason {
        private String reason;
        private Long count;
        private Double percentage;
    }

    /**
     * Retry statistics
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetryStats {
        private Long totalRetries;
        private Long successfulRetries;
        private Long failedRetries;
        private Double retrySuccessRate;
        private Double averageRetriesPerNotification;
        private Map<Integer, Long> retryAttemptDistribution;
    }

    /**
     * User activity statistics
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserActivity {
        private Long userId;
        private String userEmail;
        private Long notificationsReceived;
        private Long notificationsDelivered;
        private Double deliveryRate;
        private String preferredChannel;
    }

    /**
     * User preference statistics
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPreferenceStats {
        private Long totalUsers;
        private Long usersWithEmailEnabled;
        private Long usersWithPushEnabled;
        private Long usersWithSmsEnabled;
        private Long usersWithInAppEnabled;
        private Long usersWithMarketingEnabled;
        private Long usersWithDailyDigestEnabled;
        private Long usersWithQuietHoursEnabled;
        private Map<String, Long> languageDistribution;
        private Map<String, Long> timezoneDistribution;
    }

    // =============================================================================
    // Utility Methods
    // =============================================================================

    /**
     * Calculate overall success rate
     */
    public void calculateSuccessRate() {
        if (totalNotifications != null && totalNotifications > 0) {
            this.deliverySuccessRate = (double) deliveredNotifications / totalNotifications * 100;
            this.failureRate = (double) failedNotifications / totalNotifications * 100;
        }
    }

    /**
     * Get total processing time for the analytics period
     */
    public Long getAnalyticsPeriodDurationMs() {
        if (startDate != null && endDate != null) {
            return java.time.Duration.between(startDate, endDate).toMillis();
        }
        return null;
    }

    /**
     * Get notifications per day average
     */
    public Double getNotificationsPerDay() {
        if (totalNotifications != null && startDate != null && endDate != null) {
            long days = java.time.Duration.between(startDate, endDate).toDays();
            if (days > 0) {
                return (double) totalNotifications / days;
            }
        }
        return null;
    }

    /**
     * Get notifications per hour average
     */
    public Double getNotificationsPerHour() {
        if (totalNotifications != null && startDate != null && endDate != null) {
            long hours = java.time.Duration.between(startDate, endDate).toHours();
            if (hours > 0) {
                return (double) totalNotifications / hours;
            }
        }
        return null;
    }
}
