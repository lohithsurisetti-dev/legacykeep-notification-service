#!/bin/bash

echo "🧪 Testing Email Delivery to lohithsurisetty@gmail.com"
echo "=================================================="

# Test 1: Simple email test
echo "📧 Test 1: Simple Email Test"
curl -X POST http://localhost:8083/api/v1/email-test/send-simple \
  -H "Content-Type: application/json" \
  -d '{
    "to": "lohithsurisetty@gmail.com",
    "subject": "Test Email from LegacyKeep",
    "message": "Hello! This is a test email from the LegacyKeep Notification Service. The Kafka consumer is working perfectly!"
  }'

echo -e "\n\n"

# Test 2: HTML email test
echo "📧 Test 2: HTML Email Test"
curl -X POST http://localhost:8083/api/v1/email-test/send-mime \
  -H "Content-Type: application/json" \
  -d '{
    "to": "lohithsurisetty@gmail.com",
    "subject": "HTML Test Email from LegacyKeep",
    "htmlContent": "<html><body><h1>Hello from LegacyKeep!</h1><p>This is an HTML test email. The Kafka consumer is working perfectly!</p><p>Best regards,<br>LegacyKeep Team</p></body></html>"
  }'

echo -e "\n\n"

# Test 3: Simulate user registration event
echo "📧 Test 3: Simulate User Registration Event"
curl -X POST http://localhost:8083/api/v1/simulator/user-registration \
  -H "Content-Type: application/json" \
  -d '{
    "email": "lohithsurisetty@gmail.com",
    "userName": "Lohith Surisetti"
  }'

echo -e "\n\n"

# Test 4: Simulate welcome email
echo "📧 Test 4: Simulate Welcome Email"
curl -X POST http://localhost:8083/api/v1/simulator/welcome-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "lohithsurisetty@gmail.com",
    "userName": "Lohith Surisetti"
  }'

echo -e "\n\n"

# Test 5: Simulate password reset
echo "📧 Test 5: Simulate Password Reset"
curl -X POST http://localhost:8083/api/v1/simulator/password-reset \
  -H "Content-Type: application/json" \
  -d '{
    "email": "lohithsurisetty@gmail.com",
    "userName": "Lohith Surisetti"
  }'

echo -e "\n\n"

echo "✅ Email tests completed! Check your inbox at lohithsurisetty@gmail.com"
echo "📧 You should receive 5 test emails if the service is working properly."
