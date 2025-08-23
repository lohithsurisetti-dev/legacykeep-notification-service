#!/bin/bash

# =============================================================================
# LegacyKeep Notification Service - Email Testing Script
# =============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SERVICE_URL="http://localhost:8083"
API_BASE="$SERVICE_URL/api/v1"

echo -e "${BLUE}=============================================================================${NC}"
echo -e "${BLUE}LegacyKeep Notification Service - Email Testing${NC}"
echo -e "${BLUE}=============================================================================${NC}"

# Function to check if service is running
check_service() {
    echo -e "${YELLOW}Checking if Notification Service is running...${NC}"
    
    if curl -s "$SERVICE_URL/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Notification Service is running${NC}"
        return 0
    else
        echo -e "${RED}❌ Notification Service is not running${NC}"
        echo -e "${YELLOW}Please start the service first: mvn spring-boot:run${NC}"
        return 1
    fi
}

# Function to test email service health
test_email_health() {
    echo -e "\n${YELLOW}Testing Email Service Health...${NC}"
    
    response=$(curl -s "$API_BASE/test/email/health")
    
    if echo "$response" | grep -q '"success":true'; then
        echo -e "${GREEN}✅ Email service is healthy${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        echo -e "${RED}❌ Email service health check failed${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    fi
}

# Function to test email verification template
test_email_verification() {
    local email="$1"
    local name="$2"
    
    echo -e "\n${YELLOW}Testing Email Verification Template...${NC}"
    echo -e "Sending to: ${BLUE}$email${NC}"
    echo -e "User Name: ${BLUE}$name${NC}"
    
    payload=$(cat <<EOF
{
    "toEmail": "$email",
    "userName": "$name"
}
EOF
)
    
    response=$(curl -s -X POST "$API_BASE/test/email/verification" \
        -H "Content-Type: application/json" \
        -d "$payload")
    
    if echo "$response" | grep -q '"success":true'; then
        echo -e "${GREEN}✅ Email verification test sent successfully${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        echo -e "${RED}❌ Email verification test failed${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    fi
}

# Function to test password reset template
test_password_reset() {
    local email="$1"
    local name="$2"
    
    echo -e "\n${YELLOW}Testing Password Reset Template...${NC}"
    echo -e "Sending to: ${BLUE}$email${NC}"
    echo -e "User Name: ${BLUE}$name${NC}"
    
    payload=$(cat <<EOF
{
    "toEmail": "$email",
    "userName": "$name"
}
EOF
)
    
    response=$(curl -s -X POST "$API_BASE/test/email/password-reset" \
        -H "Content-Type: application/json" \
        -d "$payload")
    
    if echo "$response" | grep -q '"success":true'; then
        echo -e "${GREEN}✅ Password reset test sent successfully${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        echo -e "${RED}❌ Password reset test failed${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    fi
}

# Function to test notification API
test_notification_api() {
    echo -e "\n${YELLOW}Testing Notification API Endpoints...${NC}"
    
    # Test health endpoint
    echo -e "${BLUE}Testing health endpoint...${NC}"
    health_response=$(curl -s "$API_BASE/notifications/health")
    echo "$health_response" | jq '.' 2>/dev/null || echo "$health_response"
    
    # Test metrics endpoint
    echo -e "\n${BLUE}Testing metrics endpoint...${NC}"
    metrics_response=$(curl -s "$API_BASE/notifications/metrics")
    echo "$metrics_response" | jq '.' 2>/dev/null || echo "$metrics_response"
}

# Main execution
main() {
    # Check if service is running
    if ! check_service; then
        exit 1
    fi
    
    # Test email service health
    test_email_health
    
    # Test notification API
    test_notification_api
    
    # Interactive email testing
    echo -e "\n${BLUE}=============================================================================${NC}"
    echo -e "${BLUE}Interactive Email Testing${NC}"
    echo -e "${BLUE}=============================================================================${NC}"
    
    read -p "Enter test email address (or press Enter to skip): " test_email
    if [ -n "$test_email" ]; then
        read -p "Enter user name (or press Enter for 'Test User'): " user_name
        user_name=${user_name:-"Test User"}
        
        # Test email verification
        test_email_verification "$test_email" "$user_name"
        
        # Test password reset
        test_password_reset "$test_email" "$user_name"
        
        echo -e "\n${GREEN}✅ Email tests completed!${NC}"
        echo -e "${YELLOW}Check your email inbox for the test emails.${NC}"
    else
        echo -e "${YELLOW}Skipping email tests.${NC}"
    fi
    
    echo -e "\n${GREEN}✅ All tests completed!${NC}"
}

# Run main function
main "$@"
