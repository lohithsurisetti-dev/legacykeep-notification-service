#!/bin/bash

# =============================================================================
# LegacyKeep Notification Service - Quick Test Script
# =============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=============================================================================${NC}"
echo -e "${BLUE}LegacyKeep Notification Service - Quick Test${NC}"
echo -e "${BLUE}=============================================================================${NC}"

# Configuration
SERVICE_URL="http://localhost:8083"
API_BASE="$SERVICE_URL/api/v1"

# Function to check if service is running
check_service() {
    echo -e "${YELLOW}Checking if Notification Service is running...${NC}"
    
    if curl -s "$SERVICE_URL/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Notification Service is running${NC}"
        return 0
    else
        echo -e "${RED}❌ Notification Service is not running${NC}"
        return 1
    fi
}

# Function to test basic endpoints
test_basic_endpoints() {
    echo -e "\n${YELLOW}Testing Basic Endpoints...${NC}"
    
    # Test health endpoint
    echo -e "${BLUE}Testing health endpoint...${NC}"
    health_response=$(curl -s "$SERVICE_URL/health")
    echo "$health_response"
    
    # Test notification health endpoint
    echo -e "\n${BLUE}Testing notification health endpoint...${NC}"
    notification_health_response=$(curl -s "$API_BASE/notifications/health")
    echo "$notification_health_response"
    
    # Test metrics endpoint
    echo -e "\n${BLUE}Testing metrics endpoint...${NC}"
    metrics_response=$(curl -s "$API_BASE/notifications/metrics")
    echo "$metrics_response"
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

# Function to test email templates (without sending)
test_email_templates() {
    echo -e "\n${YELLOW}Testing Email Templates...${NC}"
    
    # Test email verification template
    echo -e "${BLUE}Testing email verification template...${NC}"
    verification_response=$(curl -s -X POST "$API_BASE/test/email/verification" \
        -H "Content-Type: application/json" \
        -d '{"toEmail": "test@example.com", "userName": "Test User"}')
    
    if echo "$verification_response" | grep -q '"success":true'; then
        echo -e "${GREEN}✅ Email verification template test successful${NC}"
    else
        echo -e "${RED}❌ Email verification template test failed${NC}"
        echo "$verification_response" | jq '.' 2>/dev/null || echo "$verification_response"
    fi
    
    # Test password reset template
    echo -e "\n${BLUE}Testing password reset template...${NC}"
    reset_response=$(curl -s -X POST "$API_BASE/test/email/password-reset" \
        -H "Content-Type: application/json" \
        -d '{"toEmail": "test@example.com", "userName": "Test User"}')
    
    if echo "$reset_response" | grep -q '"success":true'; then
        echo -e "${GREEN}✅ Password reset template test successful${NC}"
    else
        echo -e "${RED}❌ Password reset template test failed${NC}"
        echo "$reset_response" | jq '.' 2>/dev/null || echo "$reset_response"
    fi
}

# Function to show service status
show_service_status() {
    echo -e "\n${BLUE}=============================================================================${NC}"
    echo -e "${BLUE}Service Status${NC}"
    echo -e "${BLUE}=============================================================================${NC}"
    
    echo -e "${GREEN}✅ Notification Service is ready for testing!${NC}"
    echo -e "\n${YELLOW}Available Endpoints:${NC}"
    echo -e "• ${BLUE}Health Check:${NC} $SERVICE_URL/health"
    echo -e "• ${BLUE}Notification Health:${NC} $API_BASE/notifications/health"
    echo -e "• ${BLUE}Email Health:${NC} $API_BASE/test/email/health"
    echo -e "• ${BLUE}Email Verification Test:${NC} $API_BASE/test/email/verification"
    echo -e "• ${BLUE}Password Reset Test:${NC} $API_BASE/test/email/password-reset"
    echo -e "• ${BLUE}Metrics:${NC} $API_BASE/notifications/metrics"
    
    echo -e "\n${YELLOW}Next Steps:${NC}"
    echo -e "1. ${BLUE}Configure email credentials in .env file${NC}"
    echo -e "2. ${BLUE}Test with real email addresses${NC}"
    echo -e "3. ${BLUE}Run ./test-email.sh for comprehensive testing${NC}"
}

# Main execution
main() {
    # Check if service is running
    if ! check_service; then
        echo -e "\n${YELLOW}To start the service:${NC}"
        echo -e "1. ${BLUE}Run: ./setup-dev-env.sh${NC}"
        echo -e "2. ${BLUE}Run: mvn spring-boot:run${NC}"
        echo -e "3. ${BLUE}Run this script again${NC}"
        exit 1
    fi
    
    # Test basic endpoints
    test_basic_endpoints
    
    # Test email service health
    test_email_health
    
    # Test email templates
    test_email_templates
    
    # Show service status
    show_service_status
    
    echo -e "\n${GREEN}✅ Quick test completed!${NC}"
}

# Run main function
main "$@"
