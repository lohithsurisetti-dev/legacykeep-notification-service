#!/bin/bash

# =============================================================================
# LegacyKeep Notification Service - Real Email Test
# =============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=============================================================================${NC}"
echo -e "${BLUE}LegacyKeep Notification Service - Real Email Test${NC}"
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
        echo -e "${YELLOW}Please start the service first: mvn spring-boot:run${NC}"
        return 1
    fi
}

# Function to test email verification
test_email_verification() {
    echo -e "\n${YELLOW}Testing Email Verification (Real Email)...${NC}"
    
    payload=$(cat <<EOF
{
    "userId": "12345",
    "email": "lohithsurisetty@gmail.com",
    "userName": "Lohith Surisetty"
}
EOF
)
    
    echo -e "${BLUE}Sending email verification to: lohithsurisetty@gmail.com${NC}"
    
    response=$(curl -s -X POST "$API_BASE/simulate/user-registration" \
        -H "Content-Type: application/json" \
        -d "$payload")
    
    if echo "$response" | grep -q '"success":true'; then
        echo -e "${GREEN}✅ Email verification sent successfully!${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
        echo -e "\n${YELLOW}📧 Check your inbox at lohithsurisetty@gmail.com${NC}"
    else
        echo -e "${RED}❌ Email verification failed${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    fi
}

# Function to test password reset
test_password_reset() {
    echo -e "\n${YELLOW}Testing Password Reset (Real Email)...${NC}"
    
    payload=$(cat <<EOF
{
    "userId": "12345",
    "email": "lohithsurisetty@gmail.com",
    "userName": "Lohith Surisetty"
}
EOF
)
    
    echo -e "${BLUE}Sending password reset to: lohithsurisetty@gmail.com${NC}"
    
    response=$(curl -s -X POST "$API_BASE/simulate/password-reset" \
        -H "Content-Type: application/json" \
        -d "$payload")
    
    if echo "$response" | grep -q '"success":true'; then
        echo -e "${GREEN}✅ Password reset email sent successfully!${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
        echo -e "\n${YELLOW}📧 Check your inbox at lohithsurisetty@gmail.com${NC}"
    else
        echo -e "${RED}❌ Password reset email failed${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    fi
}

# Function to test welcome email
test_welcome_email() {
    echo -e "\n${YELLOW}Testing Welcome Email (Real Email)...${NC}"
    
    payload=$(cat <<EOF
{
    "userId": "12345",
    "email": "lohithsurisetty@gmail.com",
    "userName": "Lohith Surisetty"
}
EOF
)
    
    echo -e "${BLUE}Sending welcome email to: lohithsurisetty@gmail.com${NC}"
    
    response=$(curl -s -X POST "$API_BASE/simulate/welcome-email" \
        -H "Content-Type: application/json" \
        -d "$payload")
    
    if echo "$response" | grep -q '"success":true'; then
        echo -e "${GREEN}✅ Welcome email sent successfully!${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
        echo -e "\n${YELLOW}📧 Check your inbox at lohithsurisetty@gmail.com${NC}"
    else
        echo -e "${RED}❌ Welcome email failed${NC}"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    fi
}

# Function to show test options
show_test_options() {
    echo -e "\n${BLUE}=============================================================================${NC}"
    echo -e "${BLUE}Test Options${NC}"
    echo -e "${BLUE}=============================================================================${NC}"
    
    echo -e "${YELLOW}Choose a test to run:${NC}"
    echo -e "1. ${BLUE}Email Verification${NC} - Welcome email with verification link"
    echo -e "2. ${BLUE}Password Reset${NC} - Security-focused reset email"
    echo -e "3. ${BLUE}Welcome Email${NC} - New user welcome email"
    echo -e "4. ${BLUE}All Tests${NC} - Run all three email tests"
    echo -e "5. ${BLUE}Exit${NC} - Exit the test"
    
    read -p "Enter your choice (1-5): " choice
    
    case $choice in
        1)
            test_email_verification
            ;;
        2)
            test_password_reset
            ;;
        3)
            test_welcome_email
            ;;
        4)
            test_email_verification
            test_password_reset
            test_welcome_email
            ;;
        5)
            echo -e "${GREEN}Exiting...${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}Invalid choice. Please try again.${NC}"
            show_test_options
            ;;
    esac
}

# Main execution
main() {
    # Check if service is running
    if ! check_service; then
        exit 1
    fi
    
    echo -e "\n${GREEN}✅ Ready to send real emails!${NC}"
    echo -e "${YELLOW}Target Email: lohithsurisetty@gmail.com${NC}"
    echo -e "${YELLOW}From Email: legacykeep7@gmail.com${NC}"
    
    # Show test options
    show_test_options
    
    echo -e "\n${GREEN}✅ Real email test completed!${NC}"
    echo -e "${YELLOW}📧 Check your inbox for the test emails${NC}"
}

# Run main function
main "$@"
