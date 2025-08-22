package com.legacykeep.notification.repository;

import com.legacykeep.notification.entity.UserNotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserNotificationPreferences entity
 * 
 * Provides data access methods for user notification preferences including
 * CRUD operations and preference queries.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Repository
public interface UserNotificationPreferencesRepository extends JpaRepository<UserNotificationPreferences, Long> {

    // =============================================================================
    // Basic CRUD Operations
    // =============================================================================

    /**
     * Find preferences by user ID
     */
    Optional<UserNotificationPreferences> findByUserId(Long userId);

    /**
     * Check if preferences exist for user ID
     */
    boolean existsByUserId(Long userId);

    /**
     * Find preferences by user ID or create default if not exists
     */
    default UserNotificationPreferences findByUserIdOrCreateDefault(Long userId) {
        return findByUserId(userId).orElseGet(() -> {
            UserNotificationPreferences preferences = new UserNotificationPreferences(userId);
            return save(preferences);
        });
    }

    // =============================================================================
    // Channel-based Queries
    // =============================================================================

    /**
     * Find users who have email notifications enabled
     */
    List<UserNotificationPreferences> findByEmailEnabledTrue();

    /**
     * Find users who have push notifications enabled
     */
    List<UserNotificationPreferences> findByPushEnabledTrue();

    /**
     * Find users who have SMS notifications enabled
     */
    List<UserNotificationPreferences> findBySmsEnabledTrue();

    /**
     * Find users who have in-app notifications enabled
     */
    List<UserNotificationPreferences> findByInAppEnabledTrue();

    /**
     * Find users who have marketing emails enabled
     */
    List<UserNotificationPreferences> findByMarketingEmailsEnabledTrue();

    /**
     * Find users who have daily digest enabled
     */
    List<UserNotificationPreferences> findByDailyDigestEnabledTrue();

    // =============================================================================
    // Quiet Hours Queries
    // =============================================================================

    /**
     * Find users who have quiet hours enabled
     */
    List<UserNotificationPreferences> findByQuietHoursEnabledTrue();

    /**
     * Find users who have quiet hours disabled
     */
    List<UserNotificationPreferences> findByQuietHoursEnabledFalse();

    // =============================================================================
    // Language and Timezone Queries
    // =============================================================================

    /**
     * Find users by language preference
     */
    List<UserNotificationPreferences> findByLanguage(String language);

    /**
     * Find users by timezone
     */
    List<UserNotificationPreferences> findByTimezone(String timezone);

    /**
     * Find users by language and timezone
     */
    List<UserNotificationPreferences> findByLanguageAndTimezone(String language, String timezone);

    // =============================================================================
    // Analytics Queries
    // =============================================================================

    /**
     * Count users by email preference
     */
    @Query("SELECT p.emailEnabled, COUNT(p) FROM UserNotificationPreferences p GROUP BY p.emailEnabled")
    List<Object[]> countByEmailPreference();

    /**
     * Count users by push preference
     */
    @Query("SELECT p.pushEnabled, COUNT(p) FROM UserNotificationPreferences p GROUP BY p.pushEnabled")
    List<Object[]> countByPushPreference();

    /**
     * Count users by SMS preference
     */
    @Query("SELECT p.smsEnabled, COUNT(p) FROM UserNotificationPreferences p GROUP BY p.smsEnabled")
    List<Object[]> countBySmsPreference();

    /**
     * Count users by in-app preference
     */
    @Query("SELECT p.inAppEnabled, COUNT(p) FROM UserNotificationPreferences p GROUP BY p.inAppEnabled")
    List<Object[]> countByInAppPreference();

    /**
     * Count users by marketing emails preference
     */
    @Query("SELECT p.marketingEmailsEnabled, COUNT(p) FROM UserNotificationPreferences p GROUP BY p.marketingEmailsEnabled")
    List<Object[]> countByMarketingEmailsPreference();

    /**
     * Count users by daily digest preference
     */
    @Query("SELECT p.dailyDigestEnabled, COUNT(p) FROM UserNotificationPreferences p GROUP BY p.dailyDigestEnabled")
    List<Object[]> countByDailyDigestPreference();

    /**
     * Count users by quiet hours preference
     */
    @Query("SELECT p.quietHoursEnabled, COUNT(p) FROM UserNotificationPreferences p GROUP BY p.quietHoursEnabled")
    List<Object[]> countByQuietHoursPreference();

    /**
     * Count users by language
     */
    @Query("SELECT p.language, COUNT(p) FROM UserNotificationPreferences p GROUP BY p.language")
    List<Object[]> countByLanguage();

    /**
     * Count users by timezone
     */
    @Query("SELECT p.timezone, COUNT(p) FROM UserNotificationPreferences p GROUP BY p.timezone")
    List<Object[]> countByTimezone();

    // =============================================================================
    // Custom Queries
    // =============================================================================

    /**
     * Find users who have all notifications disabled
     */
    @Query("SELECT p FROM UserNotificationPreferences p WHERE p.emailEnabled = false AND p.pushEnabled = false AND p.smsEnabled = false AND p.inAppEnabled = false")
    List<UserNotificationPreferences> findUsersWithAllNotificationsDisabled();

    /**
     * Find users who have at least one notification channel enabled
     */
    @Query("SELECT p FROM UserNotificationPreferences p WHERE p.emailEnabled = true OR p.pushEnabled = true OR p.smsEnabled = true OR p.inAppEnabled = true")
    List<UserNotificationPreferences> findUsersWithAtLeastOneChannelEnabled();

    /**
     * Find users who have multiple notification channels enabled
     */
    @Query("SELECT p FROM UserNotificationPreferences p WHERE " +
           "(CASE WHEN p.emailEnabled = true THEN 1 ELSE 0 END + " +
           "CASE WHEN p.pushEnabled = true THEN 1 ELSE 0 END + " +
           "CASE WHEN p.smsEnabled = true THEN 1 ELSE 0 END + " +
           "CASE WHEN p.inAppEnabled = true THEN 1 ELSE 0 END) >= :minChannels")
    List<UserNotificationPreferences> findUsersWithMultipleChannelsEnabled(@Param("minChannels") int minChannels);

    /**
     * Find users who are likely to be in quiet hours based on their timezone
     */
    @Query("SELECT p FROM UserNotificationPreferences p WHERE p.quietHoursEnabled = true AND p.timezone = :timezone")
    List<UserNotificationPreferences> findUsersInQuietHoursByTimezone(@Param("timezone") String timezone);

    /**
     * Find users who have specific channel combinations enabled
     */
    @Query("SELECT p FROM UserNotificationPreferences p WHERE " +
           "p.emailEnabled = :emailEnabled AND " +
           "p.pushEnabled = :pushEnabled AND " +
           "p.smsEnabled = :smsEnabled AND " +
           "p.inAppEnabled = :inAppEnabled")
    List<UserNotificationPreferences> findUsersByChannelCombination(
            @Param("emailEnabled") boolean emailEnabled,
            @Param("pushEnabled") boolean pushEnabled,
            @Param("smsEnabled") boolean smsEnabled,
            @Param("inAppEnabled") boolean inAppEnabled
    );
}
