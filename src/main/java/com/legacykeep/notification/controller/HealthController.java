package com.legacykeep.notification.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller for Notification Service
 * 
 * Provides health check endpoints for monitoring and load balancer health checks.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * Basic health check endpoint
     * 
     * @return Health status message
     */
    @GetMapping
    public String health() {
        log.debug("Health check requested");
        return "notification-service is running!";
    }
}
