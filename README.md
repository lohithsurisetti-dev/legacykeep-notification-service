# 📧 LegacyKeep Notification Service

## 🎯 Overview

The Notification Service is a **critical component** of LegacyKeep that handles all communication with users through multiple channels including email, push notifications, SMS, and in-app notifications. It provides a **robust, scalable, and reliable** notification system designed for family-focused communication.

## 🏗️ Architecture

### **Technology Stack**
- **Backend**: Spring Boot 3.x + Java 17
- **Database**: PostgreSQL (primary) + Redis (caching)
- **Message Queue**: Apache Kafka (event-driven architecture)
- **Email**: Spring Mail + Thymeleaf Templates
- **Push Notifications**: Firebase Admin SDK
- **Template Engine**: Thymeleaf (responsive HTML emails)
- **Testing**: TestContainers + JUnit 5

### **Key Features**
1. **Multi-Channel Delivery** - Email, Push, SMS, In-App
2. **Template Management** - Dynamic, responsive email templates
3. **Event-Driven Architecture** - Kafka-based event processing
4. **Rate Limiting** - Configurable delivery limits
5. **Retry Logic** - Automatic retry for failed deliveries
6. **Analytics** - Delivery tracking and metrics
7. **User Preferences** - Personalized notification settings
8. **Quiet Hours** - Respect user notification preferences

## 🚀 Quick Start

### **Prerequisites**
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 14+
- Redis 6+
- Apache Kafka 3+

### **1. Setup Development Environment**

```bash
# Clone the repository
git clone <repository-url>
cd legacykeep-backend/notification-service

# Setup development environment
./setup-dev-env.sh
```

### **2. Configure Email Settings**

Edit the `.env` file and update email credentials:

```bash
# Email Configuration
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
EMAIL_FROM=noreply@legacykeep.com
```

**Email Service Options:**
- **Gmail SMTP**: Use App Password (not regular password)
- **SendGrid**: Use API key instead of password
- **Mailtrap**: For testing (no real emails sent)

### **3. Start the Service**

```bash
# Start required services (PostgreSQL, Redis, Kafka)
docker-compose up -d

# Start the Notification Service
mvn spring-boot:run
```

### **4. Test Email Functionality**

```bash
# Run comprehensive email tests
./test-email.sh
```

## 📧 Email Templates

### **Available Templates**

#### **Authentication Templates**
- ✅ **Email Verification** - Welcome email with verification link
- ✅ **Password Reset** - Security-focused reset email
- ✅ **Welcome Email** - New user onboarding
- ✅ **Account Locked** - Security notification

#### **Family Templates**
- 🔄 **Family Invitation** - Invite family members
- 🔄 **Member Joined** - New member notification
- 🔄 **Story Shared** - Story sharing notification

#### **Notification Templates**
- 🔄 **Story Created** - New story notification
- 🔄 **Family Update** - Family activity updates
- 🔄 **Reminder** - Scheduled reminders

### **Template Features**
- ✅ **Responsive Design** - Works on all devices
- ✅ **Professional Styling** - Consistent branding
- ✅ **Dynamic Content** - Variable injection
- ✅ **Security Features** - Expiry notices and warnings
- ✅ **Accessibility** - Proper contrast and readability

## 🧪 Testing

### **Test Endpoints**

#### **Email Testing**
```bash
# Test email service health
GET /api/v1/test/email/health

# Test email verification template
POST /api/v1/test/email/verification
{
  "toEmail": "test@example.com",
  "userName": "John Doe"
}

# Test password reset template
POST /api/v1/test/email/password-reset
{
  "toEmail": "test@example.com",
  "userName": "John Doe"
}
```

#### **Notification API**
```bash
# Health check
GET /api/v1/notifications/health

# Send notification
POST /api/v1/notifications/send
{
  "recipientId": "user123",
  "notificationType": "EMAIL",
  "templateId": "email-verification",
  "variables": {
    "userName": "John Doe",
    "verificationUrl": "https://legacykeep.com/verify?token=abc123"
  }
}

# Get notification by ID
GET /api/v1/notifications/{id}

# Get user notifications
GET /api/v1/notifications/user/{userId}
```

### **Automated Testing**

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run with TestContainers
mvn test -Dspring.profiles.active=test
```

## 📊 API Documentation

### **Core Endpoints**

#### **Notification Management**
- `POST /api/v1/notifications/send` - Send single notification
- `POST /api/v1/notifications/send/batch` - Send batch notifications
- `GET /api/v1/notifications/{id}` - Get notification by ID
- `GET /api/v1/notifications/user/{userId}` - Get user notifications
- `PUT /api/v1/notifications/{id}/cancel` - Cancel notification
- `POST /api/v1/notifications/{id}/retry` - Retry failed notification

#### **Administrative**
- `GET /api/v1/notifications/pending` - Get pending notifications
- `POST /api/v1/notifications/failed/retry` - Retry all failed notifications
- `GET /api/v1/notifications/health` - Service health check
- `GET /api/v1/notifications/metrics` - Service metrics

### **Response Format**

All API responses follow the standardized `ApiResponse` format:

```json
{
  "success": true,
  "statusCode": 200,
  "message": "Notification sent successfully",
  "data": {
    "notificationId": "notif_123",
    "status": "SENT",
    "sentAt": "2025-08-22T14:30:00Z"
  },
  "timestamp": "2025-08-22T14:30:00Z",
  "requestId": "req_456",
  "apiVersion": "1.0"
}
```

## 🔧 Configuration

### **Application Properties**

Key configuration options in `application.properties`:

```properties
# Service Configuration
server.port=8083
spring.application.name=notification-service

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/notification_db
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:password}

# Email Configuration
spring.mail.host=${EMAIL_HOST:smtp.gmail.com}
spring.mail.port=${EMAIL_PORT:587}
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}

# Kafka Configuration
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}

# Redis Configuration
spring.redis.host=${REDIS_HOST:localhost}
spring.redis.port=${REDIS_PORT:6379}

# Notification Settings
notification.rate-limit.max-per-minute=60
notification.rate-limit.max-per-hour=1000
notification.retry.max-attempts=3
```

### **Environment Variables**

Create a `.env` file for local development:

```bash
# Database
DB_USERNAME=postgres
DB_PASSWORD=password

# Email
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
EMAIL_FROM=noreply@legacykeep.com

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Security
JWT_SECRET=your-jwt-secret
SERVICE_TOKEN=your-service-token
```

## 🏗️ Development

### **Project Structure**

```
src/main/java/com/legacykeep/notification/
├── controller/           # REST API controllers
├── service/             # Business logic services
│   └── impl/           # Service implementations
├── repository/          # Data access layer
├── entity/             # JPA entities
├── dto/                # Data transfer objects
│   ├── request/        # Request DTOs
│   ├── response/       # Response DTOs
│   └── event/          # Event DTOs
├── config/             # Configuration classes
├── exception/          # Custom exceptions
└── NotificationServiceApplication.java
```

### **Adding New Email Templates**

1. **Create Template File**
   ```html
   <!-- src/main/resources/templates/email/category/template-name.html -->
   <!DOCTYPE html>
   <html xmlns:th="http://www.thymeleaf.org">
   <head>
       <title>Template Title</title>
   </head>
   <body>
       <!-- Template content -->
   </body>
   </html>
   ```

2. **Add Database Record**
   ```sql
   INSERT INTO notification_templates (
       template_id, name, description, notification_type, channel,
       subject_template, content_template, html_template, variables
   ) VALUES (
       'template-name',
       'Template Name',
       'Template description',
       'EMAIL',
       'EMAIL',
       'Email subject template',
       'Plain text content',
       'HTML content',
       '{"variable1": "string", "variable2": "number"}'
   );
   ```

3. **Test Template**
   ```bash
   # Add test endpoint in EmailTestController
   # Test with ./test-email.sh
   ```

## 📈 Monitoring & Health Checks

### **Health Endpoints**

- `GET /health` - Basic health check
- `GET /api/v1/notifications/health` - Notification service health
- `GET /api/v1/test/email/health` - Email service health

### **Metrics**

- `GET /api/v1/notifications/metrics` - Service metrics
- `GET /actuator/metrics` - Spring Boot metrics
- `GET /actuator/prometheus` - Prometheus metrics

### **Logging**

Configure logging levels in `application.properties`:

```properties
logging.level.com.legacykeep.notification=INFO
logging.level.org.springframework.mail=WARN
logging.level.org.apache.kafka=WARN
```

## 🚀 Deployment

### **Docker Deployment**

```bash
# Build Docker image
docker build -t legacykeep/notification-service:latest .

# Run with Docker Compose
docker-compose -f docker-compose.prod.yml up -d
```

### **Production Configuration**

1. **Update Environment Variables**
2. **Configure Production Database**
3. **Setup Email Service (SendGrid, AWS SES)**
4. **Configure Kafka Cluster**
5. **Setup Monitoring (Prometheus, Grafana)**

## 🤝 Contributing

### **Development Workflow**

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/email-template-name
   ```

2. **Follow Code Standards**
   - Use `Controller -> Service -> ServiceImpl` pattern
   - Follow naming conventions
   - Add comprehensive tests
   - Update documentation

3. **Testing**
   ```bash
   mvn clean test
   ./test-email.sh
   ```

4. **Submit Pull Request**
   - Include tests
   - Update documentation
   - Follow commit message conventions

### **Code Standards**

- **Java**: Follow Google Java Style Guide
- **Spring Boot**: Follow Spring Boot conventions
- **Testing**: Minimum 80% code coverage
- **Documentation**: Comprehensive JavaDoc and README updates

## 📞 Support

### **Getting Help**

- **Documentation**: Check this README and inline code comments
- **Issues**: Create GitHub issues for bugs and feature requests
- **Discussions**: Use GitHub Discussions for questions

### **Common Issues**

#### **Email Not Sending**
1. Check SMTP credentials in `.env`
2. Verify email service settings
3. Check application logs for errors
4. Test with `./test-email.sh`

#### **Database Connection Issues**
1. Ensure PostgreSQL is running
2. Check database credentials
3. Verify database exists
4. Run `./setup-dev-env.sh`

#### **Kafka Connection Issues**
1. Ensure Kafka is running
2. Check Kafka bootstrap servers
3. Verify topic configuration
4. Check network connectivity

## 📄 License

This project is part of the LegacyKeep platform. See the main project license for details.

---

**Built with ❤️ for preserving family memories**
