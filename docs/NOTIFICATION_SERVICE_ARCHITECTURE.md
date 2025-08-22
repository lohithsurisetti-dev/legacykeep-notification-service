# 📧 Notification Service Architecture Documentation

## 📋 Table of Contents

1. [Overview](#overview)
2. [Event-Driven Architecture](#event-driven-architecture)
3. [Kafka Event Design](#kafka-event-design)
4. [Service Components](#service-components)
5. [API Design](#api-design)
6. [Event Flow](#event-flow)
7. [Technology Stack](#technology-stack)
8. [Configuration](#configuration)
9. [Security](#security)
10. [Monitoring](#monitoring)
11. [Implementation Plan](#implementation-plan)

---

## 🎯 Overview

The LegacyKeep Notification Service is an **event-driven microservice** that handles all notification types (email, push notifications, SMS) through Kafka events. This architecture ensures **loose coupling**, **high scalability**, and **reliable message delivery** across all microservices.

### Key Features
- ✅ **Event-driven architecture** with Apache Kafka
- ✅ **Multi-channel notifications** (email, push, SMS)
- ✅ **Template engine** for consistent messaging
- ✅ **Queue management** with retry and dead letter queues
- ✅ **Delivery tracking** and analytics
- ✅ **User preferences** management
- ✅ **A/B testing** capabilities

---

## 🏗️ Event-Driven Architecture

### **Architecture Principles**

#### **1. 🎯 Loose Coupling**
- **Services don't know about each other** - they only publish/consume events
- **Notification service is completely independent** of other services
- **Event contracts** define the interface between services

#### **2. 🔄 Asynchronous Processing**
- **Non-blocking operations** - services don't wait for notifications
- **High performance** - notifications processed in background
- **Fault tolerance** - failed notifications can be retried

#### **3. 📈 Scalability**
- **Horizontal scaling** - multiple notification service instances
- **Event partitioning** - distribute load across consumers
- **Independent scaling** - scale notification service separately

#### **4. 🛡️ Reliability**
- **Event persistence** - Kafka stores all events
- **Retry mechanisms** - failed notifications are retried
- **Dead letter queues** - handle permanently failed notifications

### **Event Flow Diagram**

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Auth Service│    │User Service │    │Family Service│
│             │    │             │    │             │
│ User        │    │ Profile     │    │ Family      │
│ Registered  │    │ Updated     │    │ Invitation  │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
              ┌─────────────────┐
              │   Apache Kafka  │
              │                 │
              │ notification-   │
              │ events topic    │
              └─────────────────┘
                           │
              ┌─────────────────┐
              │Notification Svc │
              │                 │
              │ Event Consumer  │
              │ Template Engine │
              │ Delivery Engine │
              └─────────────────┘
                           │
       ┌───────────────────┼───────────────────┐
       │                   │                   │
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Email     │    │    Push     │    │    SMS      │
│  Service    │    │  Service    │    │  Service    │
└─────────────┘    └─────────────┘    └─────────────┘
```

---

## 📨 Kafka Event Design

### **Event Schema**

#### **Base Event Structure**
```json
{
  "eventId": "uuid",
  "eventType": "USER_REGISTERED",
  "eventVersion": "1.0",
  "timestamp": "2025-08-21T22:00:00Z",
  "source": "auth-service",
  "data": {
    // Event-specific data
  },
  "metadata": {
    "userId": "123",
    "correlationId": "uuid",
    "requestId": "uuid"
  }
}
```

#### **Notification Event Structure**
```json
{
  "eventId": "uuid",
  "eventType": "NOTIFICATION_REQUESTED",
  "eventVersion": "1.0",
  "timestamp": "2025-08-21T22:00:00Z",
  "source": "auth-service",
  "data": {
    "notificationType": "EMAIL",
    "templateId": "email-verification",
    "recipient": {
      "userId": "123",
      "email": "user@example.com",
      "username": "username"
    },
    "templateData": {
      "verificationUrl": "https://...",
      "expiresIn": "24 hours"
    },
    "priority": "HIGH",
    "scheduledAt": "2025-08-21T22:00:00Z"
  },
  "metadata": {
    "userId": "123",
    "correlationId": "uuid",
    "requestId": "uuid"
  }
}
```

### **Event Types**

#### **1. 🔐 Authentication Events**
```json
// User Registration
{
  "eventType": "USER_REGISTERED",
  "data": {
    "userId": "123",
    "email": "user@example.com",
    "username": "username",
    "verificationToken": "token"
  }
}

// Email Verification
{
  "eventType": "EMAIL_VERIFIED",
  "data": {
    "userId": "123",
    "email": "user@example.com"
  }
}

// Password Reset Request
{
  "eventType": "PASSWORD_RESET_REQUESTED",
  "data": {
    "userId": "123",
    "email": "user@example.com",
    "resetToken": "token"
  }
}

// Failed Login Attempt
{
  "eventType": "LOGIN_FAILED",
  "data": {
    "userId": "123",
    "email": "user@example.com",
    "attemptCount": 3,
    "ipAddress": "127.0.0.1"
  }
}
```

#### **2. 👤 User Management Events**
```json
// Profile Updated
{
  "eventType": "USER_PROFILE_UPDATED",
  "data": {
    "userId": "123",
    "changes": ["firstName", "lastName"],
    "oldValues": {...},
    "newValues": {...}
  }
}

// Account Status Changed
{
  "eventType": "ACCOUNT_STATUS_CHANGED",
  "data": {
    "userId": "123",
    "oldStatus": "PENDING_VERIFICATION",
    "newStatus": "ACTIVE"
  }
}
```

#### **3. 👨‍👩‍👧‍👦 Family Events**
```json
// Family Invitation
{
  "eventType": "FAMILY_INVITATION_SENT",
  "data": {
    "invitationId": "456",
    "inviterId": "123",
    "inviteeEmail": "invitee@example.com",
    "familyId": "789",
    "invitationToken": "token"
  }
}

// Family Member Joined
{
  "eventType": "FAMILY_MEMBER_JOINED",
  "data": {
    "familyId": "789",
    "userId": "123",
    "role": "MEMBER"
  }
}
```

#### **4. 📖 Story Events**
```json
// Story Created
{
  "eventType": "STORY_CREATED",
  "data": {
    "storyId": "101",
    "authorId": "123",
    "familyId": "789",
    "title": "Story Title",
    "type": "FAMILY"
  }
}

// Story Shared
{
  "eventType": "STORY_SHARED",
  "data": {
    "storyId": "101",
    "authorId": "123",
    "recipientIds": ["456", "789"],
    "shareType": "FAMILY"
  }
}
```

---

## 🔧 Service Components

### **1. 📨 Event Consumer**
```java
@Component
public class NotificationEventConsumer {
    
    @KafkaListener(topics = "notification-events")
    public void handleNotificationEvent(NotificationEvent event) {
        // Process notification event
        // Route to appropriate notification service
        // Handle retries and errors
    }
}
```

### **2. 📧 Email Service**
```java
@Service
public class EmailService {
    
    public void sendEmail(EmailNotification notification) {
        // SMTP configuration
        // Template rendering
        // Email sending
        // Delivery tracking
    }
}
```

### **3. 📱 Push Notification Service**
```java
@Service
public class PushNotificationService {
    
    public void sendPushNotification(PushNotification notification) {
        // FCM/APNS integration
        // Device token management
        // Push notification sending
        // Delivery tracking
    }
}
```

### **4. 📨 SMS Service**
```java
@Service
public class SmsService {
    
    public void sendSms(SmsNotification notification) {
        // SMS provider integration
        // SMS sending
        // Delivery tracking
    }
}
```

### **5. 🎨 Template Engine**
```java
@Service
public class TemplateEngine {
    
    public String renderTemplate(String templateId, Map<String, Object> data) {
        // Template loading
        // Variable substitution
        // HTML/text rendering
    }
}
```

### **6. 📊 Notification Manager**
```java
@Service
public class NotificationManager {
    
    public void processNotification(NotificationEvent event) {
        // Event routing
        // Template selection
        // Channel selection
        // Delivery orchestration
    }
}
```

---

## 🌐 API Design

### **REST Endpoints (For Direct Integration)**

#### **1. 📧 Email Endpoints**
```http
POST /api/v1/notifications/email
Content-Type: application/json

{
  "templateId": "email-verification",
  "recipient": {
    "email": "user@example.com",
    "userId": "123"
  },
  "templateData": {
    "verificationUrl": "https://...",
    "expiresIn": "24 hours"
  },
  "priority": "HIGH"
}
```

#### **2. 📱 Push Notification Endpoints**
```http
POST /api/v1/notifications/push
Content-Type: application/json

{
  "templateId": "story-shared",
  "recipients": ["123", "456"],
  "templateData": {
    "storyTitle": "My Family Story",
    "authorName": "John Doe"
  },
  "priority": "NORMAL"
}
```

#### **3. 📊 Management Endpoints**
```http
GET /api/v1/notifications/status/{notificationId}
GET /api/v1/notifications/history/{userId}
POST /api/v1/notifications/preferences/{userId}
GET /api/v1/notifications/analytics
```

### **Health and Monitoring**
```http
GET /api/v1/health
GET /api/v1/actuator/health
GET /api/v1/actuator/metrics
```

---

## 🔄 Event Flow

### **1. 🔐 User Registration Flow**
```
1. Auth Service → Publishes USER_REGISTERED event
2. Notification Service → Consumes event
3. Notification Service → Renders email template
4. Notification Service → Sends verification email
5. Notification Service → Publishes EMAIL_SENT event
6. Auth Service → Consumes EMAIL_SENT event (optional)
```

### **2. 📧 Email Verification Flow**
```
1. User → Clicks verification link
2. Auth Service → Verifies email
3. Auth Service → Publishes EMAIL_VERIFIED event
4. Notification Service → Consumes event
5. Notification Service → Sends welcome email
6. Notification Service → Sends push notification
```

### **3. 👨‍👩‍👧‍👦 Family Invitation Flow**
```
1. Family Service → Publishes FAMILY_INVITATION_SENT event
2. Notification Service → Consumes event
3. Notification Service → Sends invitation email
4. Notification Service → Sends push notification to family members
5. Notification Service → Publishes INVITATION_NOTIFICATIONS_SENT event
```

---

## 🛠️ Technology Stack

### **Core Technologies**
- **Spring Boot 3.x** - Application framework
- **Apache Kafka** - Event streaming platform
- **Spring Kafka** - Kafka integration
- **Java 17** - Programming language
- **Maven** - Build tool

### **Email Service**
- **Spring Mail** - SMTP integration
- **Thymeleaf** - Email template engine
- **JavaMail API** - Email sending

### **Push Notifications**
- **Firebase Cloud Messaging (FCM)** - Android push notifications
- **Apple Push Notification Service (APNS)** - iOS push notifications
- **Web Push API** - Web push notifications

### **SMS Service (Future)**
- **Twilio** - SMS provider
- **AWS SNS** - Alternative SMS provider

### **Database**
- **PostgreSQL** - Notification history and user preferences
- **Redis** - Template caching and rate limiting

### **Monitoring**
- **Micrometer** - Metrics collection
- **Prometheus** - Metrics storage
- **Grafana** - Metrics visualization
- **Spring Boot Actuator** - Health checks

---

## ⚙️ Configuration

### **Application Properties**
```yaml
# Kafka Configuration
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-service
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

# Email Configuration
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${EMAIL_USERNAME}
    password: ${EMAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

# Push Notification Configuration
firebase:
  project-id: legacykeep-app
  credentials: ${FIREBASE_CREDENTIALS}

# Database Configuration
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notification_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

# Redis Configuration
spring:
  redis:
    host: localhost
    port: 6379
```

---

## 🔒 Security

### **1. 🔐 Authentication & Authorization**
- **JWT token validation** for direct API calls
- **Service-to-service authentication** for Kafka events
- **Role-based access control** for management endpoints

### **2. 🛡️ Data Protection**
- **Email encryption** for sensitive data
- **Template data sanitization** to prevent injection
- **Rate limiting** to prevent abuse

### **3. 🔍 Audit Logging**
- **All notification events** logged
- **Delivery status** tracked
- **User preferences** changes logged

---

## 📊 Monitoring

### **1. 📈 Key Metrics**
- **Notification delivery rate**
- **Email open rate**
- **Push notification click rate**
- **Template rendering time**
- **Kafka consumer lag**

### **2. 🚨 Alerts**
- **High failure rate** notifications
- **Kafka consumer lag** alerts
- **Email service** downtime
- **Push notification** service issues

### **3. 📊 Dashboards**
- **Notification volume** by type
- **Delivery success rate** by channel
- **User engagement** metrics
- **Service performance** metrics

---

## 📋 Implementation Plan

### **Phase 1: Foundation (Week 1-2)**
- [ ] **Kafka integration** setup
- [ ] **Event consumer** implementation
- [ ] **Basic email service** with SMTP
- [ ] **Template engine** with Thymeleaf
- [ ] **Database schema** for notifications

### **Phase 2: Core Features (Week 3-4)**
- [ ] **Email templates** (verification, reset, welcome)
- [ ] **Push notification service** with FCM
- [ ] **Notification manager** for event routing
- [ ] **Delivery tracking** and analytics
- [ ] **Auth Service integration** testing

### **Phase 3: Advanced Features (Week 5-6)**
- [ ] **User preferences** management
- [ ] **A/B testing** capabilities
- [ ] **Advanced analytics** dashboard
- [ ] **Rate limiting** and throttling
- [ ] **Performance optimization**

### **Phase 4: Production Ready (Week 7-8)**
- [ ] **Comprehensive testing** (unit, integration, load)
- [ ] **Security hardening**
- [ ] **Monitoring and alerting**
- [ ] **Documentation** completion
- [ ] **Production deployment**

---

## 🎯 Success Criteria

### **Technical Metrics**
- **99.9% uptime** for notification service
- **< 100ms** template rendering time
- **< 5s** email delivery time
- **< 1s** push notification delivery time
- **Zero data loss** in event processing

### **Business Metrics**
- **95% email delivery** success rate
- **80% push notification** delivery rate
- **60% email open** rate
- **40% push notification** click rate
- **< 1% notification** failure rate

---

**Last Updated**: August 21, 2025  
**Version**: 1.0.0  
**Maintainer**: LegacyKeep Team
