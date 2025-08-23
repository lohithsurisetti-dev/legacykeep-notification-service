#!/bin/bash

# =============================================================================
# LegacyKeep Notification Service - Development Environment Setup
# =============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=============================================================================${NC}"
echo -e "${BLUE}LegacyKeep Notification Service - Development Environment Setup${NC}"
echo -e "${BLUE}=============================================================================${NC}"

# Function to check if Docker is running
check_docker() {
    echo -e "${YELLOW}Checking Docker status...${NC}"
    if docker info > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Docker is running${NC}"
        return 0
    else
        echo -e "${RED}❌ Docker is not running${NC}"
        echo -e "${YELLOW}Please start Docker Desktop and try again${NC}"
        return 1
    fi
}

# Function to start required services
start_services() {
    echo -e "${YELLOW}Starting required services (PostgreSQL, Redis, Kafka)...${NC}"
    
    if [ -f "docker-compose.yml" ]; then
        echo -e "${BLUE}Starting services with docker-compose...${NC}"
        docker-compose up -d
        
        # Wait for services to be ready
        echo -e "${YELLOW}Waiting for services to be ready...${NC}"
        sleep 10
        
        echo -e "${GREEN}✅ Services started successfully${NC}"
    else
        echo -e "${RED}❌ docker-compose.yml not found${NC}"
        echo -e "${YELLOW}Please ensure you're in the correct directory${NC}"
        return 1
    fi
}

# Function to setup database
setup_database() {
    echo -e "${YELLOW}Setting up database...${NC}"
    
    if [ -f "init-scripts/setup-database.sh" ]; then
        echo -e "${BLUE}Running database setup script...${NC}"
        chmod +x init-scripts/setup-database.sh
        ./init-scripts/setup-database.sh
        
        echo -e "${GREEN}✅ Database setup completed${NC}"
    else
        echo -e "${YELLOW}Database setup script not found, skipping...${NC}"
    fi
}

# Function to compile the application
compile_application() {
    echo -e "${YELLOW}Compiling Notification Service...${NC}"
    
    if mvn clean compile; then
        echo -e "${GREEN}✅ Compilation successful${NC}"
    else
        echo -e "${RED}❌ Compilation failed${NC}"
        return 1
    fi
}

# Function to create environment file
create_env_file() {
    echo -e "${YELLOW}Creating environment configuration...${NC}"
    
    if [ ! -f ".env" ]; then
        cat > .env << EOF
# =============================================================================
# LegacyKeep Notification Service - Environment Variables
# =============================================================================

# Database Configuration
DB_USERNAME=postgres
DB_PASSWORD=password

# Email Configuration (Update these with your email service credentials)
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
EMAIL_FROM=noreply@legacykeep.com

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Application Configuration
APP_BASE_URL=http://localhost:3000

# Security Configuration
JWT_SECRET=legacykeep-notification-secret-key-change-in-production
SERVICE_TOKEN=legacykeep-service-token-change-in-production

# Firebase Configuration (Optional for now)
FIREBASE_PROJECT_ID=legacykeep-app
FIREBASE_CREDENTIALS_PATH=classpath:firebase-credentials.json
EOF
        echo -e "${GREEN}✅ Environment file created: .env${NC}"
        echo -e "${YELLOW}⚠️  Please update the email credentials in .env file${NC}"
    else
        echo -e "${BLUE}Environment file already exists${NC}"
    fi
}

# Function to show next steps
show_next_steps() {
    echo -e "\n${BLUE}=============================================================================${NC}"
    echo -e "${BLUE}Next Steps${NC}"
    echo -e "${BLUE}=============================================================================${NC}"
    
    echo -e "${GREEN}✅ Development environment is ready!${NC}"
    echo -e "\n${YELLOW}To start testing:${NC}"
    echo -e "1. ${BLUE}Update email credentials in .env file${NC}"
    echo -e "2. ${BLUE}Start the service: mvn spring-boot:run${NC}"
    echo -e "3. ${BLUE}Run email tests: ./test-email.sh${NC}"
    
    echo -e "\n${YELLOW}Email Configuration Options:${NC}"
    echo -e "• ${BLUE}Gmail SMTP:${NC} Use App Password (not regular password)"
    echo -e "• ${BLUE}SendGrid:${NC} Use API key instead of password"
    echo -e "• ${BLUE}Mailtrap:${NC} For testing (no real emails sent)"
    
    echo -e "\n${YELLOW}Test Endpoints:${NC}"
    echo -e "• ${BLUE}Health Check:${NC} http://localhost:8083/health"
    echo -e "• ${BLUE}Email Health:${NC} http://localhost:8083/api/v1/test/email/health"
    echo -e "• ${BLUE}Notification API:${NC} http://localhost:8083/api/v1/notifications/health"
    
    echo -e "\n${GREEN}Happy testing! 🚀${NC}"
}

# Main execution
main() {
    echo -e "${YELLOW}Setting up development environment...${NC}"
    
    # Check Docker
    if ! check_docker; then
        exit 1
    fi
    
    # Start services
    if ! start_services; then
        exit 1
    fi
    
    # Setup database
    setup_database
    
    # Compile application
    if ! compile_application; then
        exit 1
    fi
    
    # Create environment file
    create_env_file
    
    # Show next steps
    show_next_steps
}

# Run main function
main "$@"
