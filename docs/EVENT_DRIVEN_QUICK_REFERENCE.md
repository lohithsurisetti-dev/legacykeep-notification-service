# 📨 Event-Driven Notification Service - Quick Reference

## 🚀 Quick Start

### **Event Flow Overview**
```
Service → Publishes Event → Kafka → Notification Service → Sends Notification
```

### **Key Concepts**
- **🎯 Loose Coupling**: Services communicate only through events
- **🔄 Asynchronous**: Non-blocking notification processing
- **📈 Scalable**: Multiple notification service instances
- **🛡️ Reliable**: Event persistence and retry mechanisms

---

## 📨 Kafka Event Structure

### **Base Event Schema**
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

### **Notification Event Schema**
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
      "email": "user@example.com"
    },
    "templateData": {
      "verificationUrl": "https://...",
      "expiresIn": "24 hours"
    },
    "priority": "HIGH"
  },
  "metadata": {
    "userId": "123",
    "correlationId": "uuid"
  }
}
```

---

## 🔐 Authentication Events

### **User Registration**
```json
{
  "eventType": "USER_REGISTERED",
  "data": {
    "userId": "123",
    "email": "user@example.com",
    "username": "username",
    "verificationToken": "token"
  }
}
```

### **Email Verification**
```json
{
  "eventType": "EMAIL_VERIFIED",
  "data": {
    "userId": "123",
    "email": "user@example.com"
  }
}
```

### **Password Reset Request**
```json
{
  "eventType": "PASSWORD_RESET_REQUESTED",
  "data": {
    "userId": "123",
    "email": "user@example.com",
    "resetToken": "token"
  }
}
```

### **Failed Login Attempt**
```json
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

---

## 👤 User Management Events

### **Profile Updated**
```json
{
  "eventType": "USER_PROFILE_UPDATED",
  "data": {
    "userId": "123",
    "changes": ["firstName", "lastName"],
    "oldValues": {...},
    "newValues": {...}
  }
}
```

### **Account Status Changed**
```json
{
  "eventType": "ACCOUNT_STATUS_CHANGED",
  "data": {
    "userId": "123",
    "oldStatus": "PENDING_VERIFICATION",
    "newStatus": "ACTIVE"
  }
}
```

---

## 👨‍👩‍👧‍👦 Family Events

### **Family Invitation**
```json
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
```

### **Family Member Joined**
```json
{
  "eventType": "FAMILY_MEMBER_JOINED",
  "data": {
    "familyId": "789",
    "userId": "123",
    "role": "MEMBER"
  }
}
```

---

## 📖 Story Events

### **Story Created**
```json
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
```

### **Story Shared**
```json
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

## 🌐 API Endpoints

### **Direct Integration (REST)**

#### **Send Email**
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

#### **Send Push Notification**
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

#### **Get Notification Status**
```http
GET /api/v1/notifications/status/{notificationId}
GET /api/v1/notifications/history/{userId}
GET /api/v1/notifications/analytics
```

---

## 🔄 Event Flow Examples

### **1. User Registration Flow**
```
1. Auth Service → Publishes USER_REGISTERED event
2. Notification Service → Consumes event
3. Notification Service → Renders email template
4. Notification Service → Sends verification email
5. Notification Service → Publishes EMAIL_SENT event
```

### **2. Email Verification Flow**
```
1. User → Clicks verification link
2. Auth Service → Verifies email
3. Auth Service → Publishes EMAIL_VERIFIED event
4. Notification Service → Consumes event
5. Notification Service → Sends welcome email
6. Notification Service → Sends push notification
```

### **3. Family Invitation Flow**
```
1. Family Service → Publishes FAMILY_INVITATION_SENT event
2. Notification Service → Consumes event
3. Notification Service → Sends invitation email
4. Notification Service → Sends push notification to family members
```

---

## ⚙️ Configuration

### **Kafka Configuration**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-service
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

### **Email Configuration**
```yaml
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
```

### **Push Notification Configuration**
```yaml
firebase:
  project-id: legacykeep-app
  credentials: ${FIREBASE_CREDENTIALS}
```

---

## 📊 Monitoring

### **Key Metrics**
- **Notification delivery rate**
- **Email open rate**
- **Push notification click rate**
- **Template rendering time**
- **Kafka consumer lag**

### **Health Endpoints**
```http
GET /api/v1/health
GET /api/v1/actuator/health
GET /api/v1/actuator/metrics
```

---

## 🛠️ Technology Stack

### **Core**
- **Spring Boot 3.x** - Application framework
- **Apache Kafka** - Event streaming
- **Spring Kafka** - Kafka integration
- **Java 17** - Programming language

### **Email**
- **Spring Mail** - SMTP integration
- **Thymeleaf** - Email templates

### **Push Notifications**
- **Firebase Cloud Messaging (FCM)** - Android
- **Apple Push Notification Service (APNS)** - iOS

### **Database**
- **PostgreSQL** - Notification history
- **Redis** - Template caching

---

## 🎯 Implementation Phases

### **Phase 1: Foundation (Week 1-2)**
- [ ] Kafka integration setup
- [ ] Event consumer implementation
- [ ] Basic email service
- [ ] Template engine

### **Phase 2: Core Features (Week 3-4)**
- [ ] Email templates
- [ ] Push notification service
- [ ] Notification manager
- [ ] Delivery tracking

### **Phase 3: Advanced Features (Week 5-6)**
- [ ] User preferences
- [ ] A/B testing
- [ ] Advanced analytics
- [ ] Performance optimization

---

## 🔗 Related Documentation

- **[Complete Architecture Documentation](./NOTIFICATION_SERVICE_ARCHITECTURE.md)**
- **[Kafka Event Design](./KAFKA_EVENT_DESIGN.md)**
- **[Template Engine Guide](./TEMPLATE_ENGINE_GUIDE.md)**
- **[Integration Guide](./INTEGRATION_GUIDE.md)**

---

**Last Updated**: August 21, 2025  
**Version**: 1.0.0
