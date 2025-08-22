# 📧 Notification Service

## 📋 Description
The LegacyKeep Notification Service is an **event-driven microservice** that handles all notification types (email, push notifications, SMS) through Kafka events. This service ensures **loose coupling**, **high scalability**, and **reliable message delivery** across all microservices.

## 🛠️ Technology Stack
- **Spring Boot 3.x** - Application framework
- **Apache Kafka** - Event streaming platform
- **Spring Kafka** - Kafka integration
- **Java 17** - Programming language
- **Maven** - Build tool

## 🚀 Quick Start
```bash
mvn spring-boot:run
```

## 📚 Documentation
- **[📖 Complete Documentation](./docs/README.md)** - Full documentation index
- **[⚡ Quick Reference](./docs/EVENT_DRIVEN_QUICK_REFERENCE.md)** - Event-driven architecture guide
- **[🏗️ Architecture](./docs/NOTIFICATION_SERVICE_ARCHITECTURE.md)** - System design and architecture

## 🔗 API Endpoints
- **Health Check**: http://localhost:8080/api/v1/health
- **Actuator**: http://localhost:8080/actuator/health
- **API Documentation**: http://localhost:8080/swagger-ui.html (when implemented)

## 🎯 Key Features
- ✅ **Event-driven architecture** with Apache Kafka
- ✅ **Multi-channel notifications** (email, push, SMS)
- ✅ **Template engine** for consistent messaging
- ✅ **Queue management** with retry and dead letter queues
- ✅ **Delivery tracking** and analytics
- ✅ **User preferences** management
- ✅ **A/B testing** capabilities

## 🔄 Event Flow
```
Service → Publishes Event → Kafka → Notification Service → Sends Notification
```

## 📊 Status
- **Foundation**: ✅ Ready
- **Kafka Integration**: 📋 Planned
- **Email Service**: 📋 Planned
- **Push Notifications**: 📋 Planned
