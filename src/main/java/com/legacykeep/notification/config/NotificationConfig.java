package com.legacykeep.notification.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Comprehensive Notification Configuration
 * 
 * Provides dynamic configuration management for the notification service,
 * focusing on email-first but extensible for all channels.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "notification")
@Data
public class NotificationConfig {

    // =============================================================================
    // Email Configuration
    // =============================================================================

    private EmailConfig email = new EmailConfig();

    // =============================================================================
    // Kafka Configuration
    // =============================================================================

    private KafkaConfig kafka = new KafkaConfig();

    // =============================================================================
    // Redis Configuration
    // =============================================================================

    private RedisConfig redis = new RedisConfig();

    // =============================================================================
    // Async Configuration
    // =============================================================================

    private AsyncConfig async = new AsyncConfig();

    // =============================================================================
    // Rate Limiting Configuration
    // =============================================================================

    private RateLimitConfig rateLimit = new RateLimitConfig();

    // =============================================================================
    // Template Configuration
    // =============================================================================

    private TemplateConfig template = new TemplateConfig();

    // =============================================================================
    // Bean Configurations
    // =============================================================================

    /**
     * Configure JavaMailSender for email delivery.
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // Set basic properties
        mailSender.setHost(email.getSmtp().getHost());
        mailSender.setPort(email.getSmtp().getPort());
        mailSender.setUsername(email.getSmtp().getUsername());
        mailSender.setPassword(email.getSmtp().getPassword());
        mailSender.setProtocol(email.getSmtp().getProtocol());

        // Set JavaMail properties
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", email.getSmtp().getProtocol());
        props.put("mail.smtp.auth", email.getSmtp().isAuth());
        props.put("mail.smtp.starttls.enable", email.getSmtp().isStartTls());
        props.put("mail.smtp.ssl.enable", email.getSmtp().isSsl());
        props.put("mail.smtp.connectiontimeout", email.getSmtp().getConnectionTimeout());
        props.put("mail.smtp.timeout", email.getSmtp().getTimeout());
        props.put("mail.smtp.writetimeout", email.getSmtp().getWriteTimeout());
        props.put("mail.debug", email.getSmtp().isDebug());

        log.info("JavaMailSender configured: host={}, port={}, username={}", 
                email.getSmtp().getHost(), email.getSmtp().getPort(), email.getSmtp().getUsername());

        return mailSender;
    }

    /**
     * Configure TemplateEngine for dynamic template processing.
     */
    @Bean
    public TemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        
        // Configure string template resolver for dynamic templates
        StringTemplateResolver stringResolver = new StringTemplateResolver();
        stringResolver.setTemplateMode("HTML");
        stringResolver.setCacheable(false); // Disable cache for dynamic templates
        
        templateEngine.addTemplateResolver(stringResolver);
        templateEngine.setEnableSpringELCompiler(true);

        log.info("TemplateEngine configured with dynamic template support");

        return templateEngine;
    }

    /**
     * Configure async task executor for notification processing.
     */
    @Bean("notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(async.getCorePoolSize());
        executor.setMaxPoolSize(async.getMaxPoolSize());
        executor.setQueueCapacity(async.getQueueCapacity());
        executor.setThreadNamePrefix("notification-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        log.info("Notification task executor configured: core={}, max={}, queue={}", 
                async.getCorePoolSize(), async.getMaxPoolSize(), async.getQueueCapacity());

        return executor;
    }

    // =============================================================================
    // Inner Configuration Classes
    // =============================================================================

    /**
     * Email configuration properties.
     */
    @Data
    public static class EmailConfig {
        private SmtpConfig smtp = new SmtpConfig();
        private SenderConfig sender = new SenderConfig();
        private TemplateConfig template = new TemplateConfig();
        private RateLimitConfig rateLimit = new RateLimitConfig();
        private AttachmentConfig attachment = new AttachmentConfig();
    }

    /**
     * SMTP configuration properties.
     */
    @Data
    public static class SmtpConfig {
        private String host = "localhost";
        private int port = 587;
        private String username = "";
        private String password = "";
        private String protocol = "smtp";
        private boolean auth = true;
        private boolean startTls = true;
        private boolean ssl = false;
        private int connectionTimeout = 5000;
        private int timeout = 5000;
        private int writeTimeout = 5000;
        private boolean debug = false;
    }

    /**
     * Email sender configuration properties.
     */
    @Data
    public static class SenderConfig {
        private String name = "LegacyKeep";
        private String address = "noreply@legacykeep.com";
        private String replyTo = "support@legacykeep.com";
    }

    /**
     * Email template configuration properties.
     */
    @Data
    public static class TemplateConfig {
        private String type = "HTML";
        private String encoding = "UTF-8";
        private boolean cacheEnabled = false;
        private int cacheSize = 100;
    }

    /**
     * Email rate limiting configuration properties.
     */
    @Data
    public static class RateLimitConfig {
        private boolean enabled = true;
        private int maxPerMinute = 60;
        private int maxPerHour = 1000;
        private int maxPerDay = 10000;
        private String storageType = "REDIS"; // REDIS, MEMORY
    }

    /**
     * Email attachment configuration properties.
     */
    @Data
    public static class AttachmentConfig {
        private int maxAttachments = 5;
        private long maxAttachmentSize = 10485760; // 10MB
        private String[] allowedTypes = {"pdf", "doc", "docx", "txt", "jpg", "jpeg", "png"};
    }

    /**
     * Kafka configuration properties.
     */
    @Data
    public static class KafkaConfig {
        private String bootstrapServers = "localhost:9092";
        private String groupId = "notification-service";
        private String autoOffsetReset = "earliest";
        private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String valueDeserializer = "org.springframework.kafka.support.serializer.JsonDeserializer";
        private String trustPackages = "com.legacykeep.notification.dto.event";
        private TopicsConfig topics = new TopicsConfig();
    }

    /**
     * Kafka topics configuration properties.
     */
    @Data
    public static class TopicsConfig {
        private String notificationEvents = "notification-events";
        private String notificationCommands = "notification-commands";
        private String notificationResponses = "notification-responses";
        private int partitions = 3;
        private short replicationFactor = 1;
    }

    /**
     * Redis configuration properties.
     */
    @Data
    public static class RedisConfig {
        private String host = "localhost";
        private int port = 6379;
        private String password = "";
        private int database = 0;
        private int connectionTimeout = 2000;
        private int readTimeout = 2000;
        private boolean ssl = false;
    }

    /**
     * Async configuration properties.
     */
    @Data
    public static class AsyncConfig {
        private int corePoolSize = 5;
        private int maxPoolSize = 20;
        private int queueCapacity = 100;
        private int keepAliveSeconds = 60;
    }

    // =============================================================================
    // Configuration Validation Methods
    // =============================================================================

    /**
     * Validate email configuration.
     */
    public boolean isEmailConfigValid() {
        return email != null &&
               email.getSmtp() != null &&
               email.getSmtp().getHost() != null &&
               !email.getSmtp().getHost().trim().isEmpty() &&
               email.getSmtp().getPort() > 0 &&
               email.getSmtp().getPort() <= 65535;
    }

    /**
     * Validate Kafka configuration.
     */
    public boolean isKafkaConfigValid() {
        return kafka != null &&
               kafka.getBootstrapServers() != null &&
               !kafka.getBootstrapServers().trim().isEmpty() &&
               kafka.getGroupId() != null &&
               !kafka.getGroupId().trim().isEmpty();
    }

    /**
     * Validate Redis configuration.
     */
    public boolean isRedisConfigValid() {
        return redis != null &&
               redis.getHost() != null &&
               !redis.getHost().trim().isEmpty() &&
               redis.getPort() > 0 &&
               redis.getPort() <= 65535;
    }

    /**
     * Get configuration summary for logging.
     */
    public String getConfigurationSummary() {
        return String.format(
                "NotificationConfig{email=%s, kafka=%s, redis=%s, async=%s}",
                isEmailConfigValid() ? "VALID" : "INVALID",
                isKafkaConfigValid() ? "VALID" : "INVALID",
                isRedisConfigValid() ? "VALID" : "INVALID",
                async != null ? "CONFIGURED" : "DEFAULT"
        );
    }

    // =============================================================================
    // Dynamic Configuration Update Methods
    // =============================================================================

    /**
     * Update email SMTP configuration dynamically.
     */
    public void updateSmtpConfig(String host, Integer port, String username, String password) {
        if (email == null) {
            email = new EmailConfig();
        }
        if (email.getSmtp() == null) {
            email.setSmtp(new SmtpConfig());
        }

        if (host != null) email.getSmtp().setHost(host);
        if (port != null) email.getSmtp().setPort(port);
        if (username != null) email.getSmtp().setUsername(username);
        if (password != null) email.getSmtp().setPassword(password);

        log.info("SMTP configuration updated: host={}, port={}, username={}", 
                email.getSmtp().getHost(), email.getSmtp().getPort(), email.getSmtp().getUsername());
    }

    /**
     * Update email sender configuration dynamically.
     */
    public void updateSenderConfig(String name, String address, String replyTo) {
        if (email == null) {
            email = new EmailConfig();
        }
        if (email.getSender() == null) {
            email.setSender(new SenderConfig());
        }

        if (name != null) email.getSender().setName(name);
        if (address != null) email.getSender().setAddress(address);
        if (replyTo != null) email.getSender().setReplyTo(replyTo);

        log.info("Sender configuration updated: name={}, address={}, replyTo={}", 
                email.getSender().getName(), email.getSender().getAddress(), email.getSender().getReplyTo());
    }

    /**
     * Update rate limiting configuration dynamically.
     */
    public void updateRateLimitConfig(boolean enabled, Integer maxPerMinute, Integer maxPerHour, Integer maxPerDay) {
        if (email == null) {
            email = new EmailConfig();
        }
        if (email.getRateLimit() == null) {
            email.setRateLimit(new RateLimitConfig());
        }

        email.getRateLimit().setEnabled(enabled);
        if (maxPerMinute != null) email.getRateLimit().setMaxPerMinute(maxPerMinute);
        if (maxPerHour != null) email.getRateLimit().setMaxPerHour(maxPerHour);
        if (maxPerDay != null) email.getRateLimit().setMaxPerDay(maxPerDay);

        log.info("Rate limit configuration updated: enabled={}, maxPerMinute={}, maxPerHour={}, maxPerDay={}", 
                email.getRateLimit().isEnabled(), email.getRateLimit().getMaxPerMinute(), 
                email.getRateLimit().getMaxPerHour(), email.getRateLimit().getMaxPerDay());
    }
}








