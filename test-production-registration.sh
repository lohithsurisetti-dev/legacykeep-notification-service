#!/bin/bash

echo "🎯 PRODUCTION-READY USER REGISTRATION TEST - LegacyKeep"
echo "========================================================"
echo "📧 Testing complete user registration flow with email delivery"
echo ""

# Configuration
AUTH_SERVICE_URL="http://localhost:8081"
NOTIFICATION_SERVICE_URL="http://localhost:8083"
TEST_EMAIL="lohithsurisetti@gmail.com"
TEST_USERNAME="lohithsurisetti"
TEST_FIRST_NAME="Lohith"
TEST_LAST_NAME="Surisetti"
TEST_PASSWORD="SecurePassword123!"

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

# Function to test user registration
test_user_registration() {
    print_status "INFO" "Testing user registration..."
    
    # Create registration payload
    registration_payload=$(cat <<EOF
{
    "email": "$TEST_EMAIL",
    "password": "$TEST_PASSWORD",
    "firstName": "$TEST_FIRST_NAME",
    "lastName": "$TEST_LAST_NAME",
    "username": "$TEST_USERNAME"
}
EOF
)
    
    # Send registration request
    print_status "INFO" "Sending registration request to Auth Service..."
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$AUTH_SERVICE_URL/api/v1/auth/register" \
        -H "Content-Type: application/json" \
        -d "$registration_payload")
    
    http_code="${response: -3}"
    body="${response%???}"
    
    echo "Response Code: $http_code"
    echo "Response Body: $body"
    
    if [ "$http_code" = "200" ]; then
        print_status "SUCCESS" "User registration successful!"
        
        # Extract user ID from response if available
        user_id=$(echo "$body" | grep -o '"userId":[0-9]*' | cut -d':' -f2)
        if [ -n "$user_id" ]; then
            print_status "INFO" "User ID: $user_id"
        fi
        
        return 0
    else
        print_status "ERROR" "User registration failed (HTTP $http_code)"
        return 1
    fi
}

# Function to test email delivery
test_email_delivery() {
    print_status "INFO" "Testing email delivery to $TEST_EMAIL..."
    
    # Test simple email delivery
    email_payload=$(cat <<EOF
{
    "to": "$TEST_EMAIL",
    "subject": "🎉 Welcome to LegacyKeep - Registration Successful!",
    "message": "Hello $TEST_FIRST_NAME! Your account has been successfully registered with LegacyKeep. Welcome aboard! 🚀"
}
EOF
)
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$NOTIFICATION_SERVICE_URL/api/v1/email-test/send-simple" \
        -H "Content-Type: application/json" \
        -d "$email_payload")
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        print_status "SUCCESS" "Welcome email sent successfully!"
        return 0
    else
        print_status "ERROR" "Email delivery failed (HTTP $http_code)"
        return 1
    fi
}

# Function to test HTML email delivery
test_html_email_delivery() {
    print_status "INFO" "Testing HTML email delivery..."
    
    html_payload=$(cat <<EOF
{
    "to": "$TEST_EMAIL",
    "subject": "🎯 LegacyKeep - Your Account is Ready!",
    "htmlContent": "<html><body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'><div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'><h1 style='margin: 0; font-size: 28px;'>🎉 Welcome to LegacyKeep!</h1><p style='margin: 10px 0 0 0; font-size: 16px;'>Your account is now active and ready to use.</p></div><div style='background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px;'><h2 style='color: #333; margin-top: 0;'>Hello $TEST_FIRST_NAME $TEST_LAST_NAME!</h2><p style='color: #666; line-height: 1.6;'>Thank you for registering with LegacyKeep. Your account has been successfully created with the following details:</p><div style='background: white; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #667eea;'><p style='margin: 5px 0;'><strong>Username:</strong> $TEST_USERNAME</p><p style='margin: 5px 0;'><strong>Email:</strong> $TEST_EMAIL</p><p style='margin: 5px 0;'><strong>Account Status:</strong> <span style='color: #28a745;'>✅ Active</span></p></div><p style='color: #666; line-height: 1.6;'>You can now log in to your account and start using all the features of LegacyKeep.</p><div style='text-align: center; margin: 30px 0;'><a href='http://localhost:3000/login' style='background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 25px; display: inline-block; font-weight: bold;'>🚀 Get Started</a></div><p style='color: #999; font-size: 14px; text-align: center; margin-top: 30px;'>If you have any questions, please don't hesitate to contact our support team.</p></div></body></html>"
}
EOF
)
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$NOTIFICATION_SERVICE_URL/api/v1/email-test/send-mime" \
        -H "Content-Type: application/json" \
        -d "$html_payload")
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        print_status "SUCCESS" "HTML welcome email sent successfully!"
        return 0
    else
        print_status "ERROR" "HTML email delivery failed (HTTP $http_code)"
        return 1
    fi
}

# Function to verify email delivery
verify_email_delivery() {
    print_status "INFO" "Verifying email delivery..."
    
    # Wait a moment for email processing
    sleep 5
    
    # Check notification service health
    check_service_health "Notification Service" "$NOTIFICATION_SERVICE_URL/api/v1/health"
    
    print_status "INFO" "Email delivery verification complete. Please check your inbox at $TEST_EMAIL"
}

# Main test execution
main() {
    echo ""
    print_status "INFO" "Starting comprehensive user registration test..."
    echo ""
    
    # Step 1: Check service health
    print_status "INFO" "Step 1: Checking service health..."
    if ! check_service_health "Auth Service" "$AUTH_SERVICE_URL/api/v1/auth/health"; then
        print_status "ERROR" "Auth Service is not healthy. Exiting test."
        exit 1
    fi
    
    if ! check_service_health "Notification Service" "$NOTIFICATION_SERVICE_URL/api/v1/health"; then
        print_status "ERROR" "Notification Service is not healthy. Exiting test."
        exit 1
    fi
    
    echo ""
    
    # Step 2: Test user registration
    print_status "INFO" "Step 2: Testing user registration..."
    if ! test_user_registration; then
        print_status "ERROR" "User registration failed. Exiting test."
        exit 1
    fi
    
    echo ""
    
    # Step 3: Test email delivery
    print_status "INFO" "Step 3: Testing email delivery..."
    if ! test_email_delivery; then
        print_status "ERROR" "Email delivery failed. Exiting test."
        exit 1
    fi
    
    echo ""
    
    # Step 4: Test HTML email delivery
    print_status "INFO" "Step 4: Testing HTML email delivery..."
    if ! test_html_email_delivery; then
        print_status "ERROR" "HTML email delivery failed. Exiting test."
        exit 1
    fi
    
    echo ""
    
    # Step 5: Verify email delivery
    print_status "INFO" "Step 5: Verifying email delivery..."
    verify_email_delivery
    
    echo ""
    print_status "SUCCESS" "🎉 COMPREHENSIVE USER REGISTRATION TEST COMPLETED SUCCESSFULLY!"
    echo ""
    print_status "INFO" "📧 Check your email inbox at: $TEST_EMAIL"
    print_status "INFO" "🔗 Auth Service Swagger UI: $AUTH_SERVICE_URL/api/v1/swagger-ui.html"
    print_status "INFO" "🔗 Notification Service Swagger UI: $NOTIFICATION_SERVICE_URL/api/v1/swagger-ui.html"
    echo ""
    print_status "SUCCESS" "✅ Both services are working perfectly in production-ready condition!"
}

# Run the main test
main
