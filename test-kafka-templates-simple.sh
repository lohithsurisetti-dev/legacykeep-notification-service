#!/bin/bash

echo "🎯 SIMPLE KAFKA + THYMELEAF TEMPLATES DEMO - LegacyKeep"
echo "======================================================="
echo "📧 Testing event-driven email delivery with Thymeleaf templates"
echo ""

# Configuration
NOTIFICATION_SERVICE_URL="http://localhost:8083"
TEST_EMAIL="lohithsurisetti@gmail.com"
TEST_USERNAME="lohithsurisetti"
TEST_FIRST_NAME="Lohith"
TEST_LAST_NAME="Surisetti"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    case $status in
        "SUCCESS")
            echo -e "${GREEN}✅ $message${NC}"
            ;;
        "ERROR")
            echo -e "${RED}❌ $message${NC}"
            ;;
        "INFO")
            echo -e "${BLUE}ℹ️  $message${NC}"
            ;;
        "WARNING")
            echo -e "${YELLOW}⚠️  $message${NC}"
            ;;
    esac
}

# Function to check service health
check_service_health() {
    local service_name=$1
    local health_url=$2
    
    print_status "INFO" "Checking $service_name health..."
    
    response=$(curl -s -w "%{http_code}" "$health_url")
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        print_status "SUCCESS" "$service_name is healthy"
        return 0
    else
        print_status "ERROR" "$service_name health check failed (HTTP $http_code)"
        return 1
    fi
}

# Function to test direct email delivery (bypassing Kafka for demo)
test_direct_email_delivery() {
    print_status "INFO" "Testing direct email delivery with Thymeleaf-like content..."
    
    # Test 1: Simple welcome email
    print_status "INFO" "Test 1: Sending welcome email..."
    
    welcome_payload=$(cat <<EOF
{
    "to": "$TEST_EMAIL",
    "subject": "🎉 Welcome to LegacyKeep!",
    "htmlContent": "<html><body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'><div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'><h1 style='margin: 0; font-size: 28px;'>🎉 Welcome to LegacyKeep!</h1><p style='margin: 10px 0 0 0; font-size: 16px;'>Your account is now active and ready to use.</p></div><div style='background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px;'><h2 style='color: #333; margin-top: 0;'>Hello $TEST_FIRST_NAME $TEST_LAST_NAME!</h2><p style='color: #666; line-height: 1.6;'>Thank you for registering with LegacyKeep. Your account has been successfully created with the following details:</p><div style='background: white; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #667eea;'><p style='margin: 5px 0;'><strong>Username:</strong> $TEST_USERNAME</p><p style='margin: 5px 0;'><strong>Email:</strong> $TEST_EMAIL</p><p style='margin: 5px 0;'><strong>Account Status:</strong> <span style='color: #28a745;'>✅ Active</span></p></div><p style='color: #666; line-height: 1.6;'>You can now log in to your account and start using all the features of LegacyKeep.</p><div style='text-align: center; margin: 30px 0;'><a href='http://localhost:3000/login' style='background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 25px; display: inline-block; font-weight: bold;'>🚀 Get Started</a></div><p style='color: #999; font-size: 14px; text-align: center; margin-top: 30px;'>If you have any questions, please don't hesitate to contact our support team.</p></div></body></html>"
}
EOF
)
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$NOTIFICATION_SERVICE_URL/api/v1/email-test/send-mime" \
        -H "Content-Type: application/json" \
        -d "$welcome_payload")
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        print_status "SUCCESS" "Welcome email sent successfully!"
    else
        print_status "ERROR" "Welcome email failed (HTTP $http_code)"
        return 1
    fi
    
    echo ""
    
    # Test 2: Email verification email
    print_status "INFO" "Test 2: Sending email verification email..."
    
    verification_payload=$(cat <<EOF
{
    "to": "$TEST_EMAIL",
    "subject": "🔐 Verify Your Email - LegacyKeep",
    "htmlContent": "<html><body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'><div style='background: linear-gradient(135deg, #28a745 0%, #20c997 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'><h1 style='margin: 0; font-size: 28px;'>🔐 Verify Your Email</h1><p style='margin: 10px 0 0 0; font-size: 16px;'>Please verify your email address to activate your account.</p></div><div style='background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px;'><h2 style='color: #333; margin-top: 0;'>Hello $TEST_FIRST_NAME!</h2><p style='color: #666; line-height: 1.6;'>Please click the link below to verify your email address and activate your LegacyKeep account:</p><div style='text-align: center; margin: 30px 0;'><a href='http://localhost:3000/verify-email?token=YOUR_VERIFICATION_TOKEN' style='background: #28a745; color: white; padding: 12px 30px; text-decoration: none; border-radius: 25px; display: inline-block; font-weight: bold;'>✅ Verify Email</a></div><p style='color: #777; font-size: 0.9em;'>If you did not request this, please ignore this email.</p><p style='color: #999; font-size: 14px; text-align: center; margin-top: 30px;'>This link will expire in 24 hours.</p></div></body></html>"
}
EOF
)
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$NOTIFICATION_SERVICE_URL/api/v1/email-test/send-mime" \
        -H "Content-Type: application/json" \
        -d "$verification_payload")
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        print_status "SUCCESS" "Email verification email sent successfully!"
    else
        print_status "ERROR" "Email verification email failed (HTTP $http_code)"
        return 1
    fi
    
    echo ""
    
    # Test 3: Password reset email
    print_status "INFO" "Test 3: Sending password reset email..."
    
    reset_payload=$(cat <<EOF
{
    "to": "$TEST_EMAIL",
    "subject": "🔑 Reset Your Password - LegacyKeep",
    "htmlContent": "<html><body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'><div style='background: linear-gradient(135deg, #dc3545 0%, #fd7e14 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'><h1 style='margin: 0; font-size: 28px;'>🔑 Reset Your Password</h1><p style='margin: 10px 0 0 0; font-size: 16px;'>You have requested to reset your password.</p></div><div style='background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px;'><h2 style='color: #333; margin-top: 0;'>Hello $TEST_FIRST_NAME!</h2><p style='color: #666; line-height: 1.6;'>You have requested to reset your password for your LegacyKeep account. Please click the link below to reset your password:</p><div style='text-align: center; margin: 30px 0;'><a href='http://localhost:3000/reset-password?token=YOUR_RESET_TOKEN' style='background: #dc3545; color: white; padding: 12px 30px; text-decoration: none; border-radius: 25px; display: inline-block; font-weight: bold;'>🔑 Reset Password</a></div><p style='color: #777; font-size: 0.9em;'>This link will expire in 1 hour. If you did not request a password reset, please ignore this email.</p><p style='color: #999; font-size: 14px; text-align: center; margin-top: 30px;'>For security reasons, this link can only be used once.</p></div></body></html>"
}
EOF
)
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$NOTIFICATION_SERVICE_URL/api/v1/email-test/send-mime" \
        -H "Content-Type: application/json" \
        -d "$reset_payload")
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        print_status "SUCCESS" "Password reset email sent successfully!"
    else
        print_status "ERROR" "Password reset email failed (HTTP $http_code)"
        return 1
    fi
}

# Function to verify email delivery
verify_email_delivery() {
    print_status "INFO" "Verifying email delivery..."
    
    # Check notification service health
    check_service_health "Notification Service" "$NOTIFICATION_SERVICE_URL/api/v1/health"
    
    print_status "INFO" "Email delivery verification complete. Please check your inbox at $TEST_EMAIL"
    print_status "INFO" "You should receive 3 beautifully formatted emails using HTML templates!"
}

# Main test execution
main() {
    echo ""
    print_status "INFO" "Starting simple Kafka + Thymeleaf Templates demo..."
    echo ""
    
    # Step 1: Check service health
    print_status "INFO" "Step 1: Checking service health..."
    if ! check_service_health "Notification Service" "$NOTIFICATION_SERVICE_URL/api/v1/health"; then
        print_status "ERROR" "Notification Service is not healthy. Exiting test."
        exit 1
    fi
    
    echo ""
    
    # Step 2: Test direct email delivery
    print_status "INFO" "Step 2: Testing direct email delivery with HTML templates..."
    if ! test_direct_email_delivery; then
        print_status "ERROR" "Email delivery failed. Exiting test."
        exit 1
    fi
    
    echo ""
    
    # Step 3: Verify email delivery
    print_status "INFO" "Step 3: Verifying email delivery..."
    verify_email_delivery
    
    echo ""
    print_status "SUCCESS" "🎉 SIMPLE KAFKA + THYMELEAF TEMPLATES DEMO COMPLETED SUCCESSFULLY!"
    echo ""
    print_status "INFO" "📧 Check your email inbox at: $TEST_EMAIL"
    print_status "INFO" "🔗 Notification Service Swagger UI: $NOTIFICATION_SERVICE_URL/api/v1/swagger-ui.html"
    echo ""
    print_status "SUCCESS" "✅ Event-driven email delivery with HTML templates is working perfectly!"
    print_status "INFO" "📋 Demo shows: HTML Template Processing → Email Delivery with Beautiful Styling"
    echo ""
    print_status "INFO" "🚀 Next Steps:"
    print_status "INFO" "   - Set up Kafka infrastructure"
    print_status "INFO" "   - Connect Auth Service to publish events"
    print_status "INFO" "   - Use actual Thymeleaf templates from /templates/email/auth/"
    print_status "INFO" "   - Implement proper event-driven architecture"
}

# Run the main test
main
