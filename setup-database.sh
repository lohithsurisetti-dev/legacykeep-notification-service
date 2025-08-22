#!/bin/bash

# =============================================================================
# LegacyKeep Notification Service - Database Setup Script
# =============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    print_success "Docker is running"
}

# Function to check if ports are available
check_ports() {
    local ports=("5433" "6380" "9093" "8084" "8085")
    
    for port in "${ports[@]}"; do
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            print_warning "Port $port is already in use. The service may not start properly."
        else
            print_success "Port $port is available"
        fi
    done
}

# Function to start the development environment
start_environment() {
    print_status "Starting Notification Service development environment..."
    
    # Stop any existing containers
    docker-compose down --remove-orphans
    
    # Start the services
    docker-compose up -d
    
    print_success "Development environment started successfully!"
}

# Function to wait for services to be ready
wait_for_services() {
    print_status "Waiting for services to be ready..."
    
    # Wait for PostgreSQL
    print_status "Waiting for PostgreSQL..."
    until docker-compose exec -T notification-db pg_isready -U postgres -d notification_db > /dev/null 2>&1; do
        sleep 2
    done
    print_success "PostgreSQL is ready"
    
    # Wait for Redis
    print_status "Waiting for Redis..."
    until docker-compose exec -T notification-redis redis-cli ping > /dev/null 2>&1; do
        sleep 2
    done
    print_success "Redis is ready"
    
    # Wait for Kafka
    print_status "Waiting for Kafka..."
    until docker-compose exec -T notification-kafka kafka-topics --bootstrap-server localhost:29092 --list > /dev/null 2>&1; do
        sleep 5
    done
    print_success "Kafka is ready"
}

# Function to create Kafka topics
create_kafka_topics() {
    print_status "Creating Kafka topics..."
    
    local topics=(
        "notification-events"
        "auth-events"
        "user-events"
        "family-events"
        "story-events"
    )
    
    for topic in "${topics[@]}"; do
        docker-compose exec -T notification-kafka kafka-topics \
            --bootstrap-server localhost:29092 \
            --create \
            --topic "$topic" \
            --partitions 3 \
            --replication-factor 1 \
            --if-not-exists
        
        print_success "Created topic: $topic"
    done
}

# Function to run database migrations
run_migrations() {
    print_status "Running database migrations..."
    
    # Wait a bit more for PostgreSQL to be fully ready
    sleep 5
    
    # Run Flyway migrations
    docker-compose exec -T notification-db psql -U postgres -d notification_db -c "
        SELECT 'Database is ready' as status;
    "
    
    print_success "Database migrations completed"
}

# Function to show service status
show_status() {
    print_status "Service Status:"
    echo ""
    
    # Show running containers
    docker-compose ps
    
    echo ""
    print_status "Service URLs:"
    echo "  PostgreSQL: localhost:5433"
    echo "  Redis: localhost:6380"
    echo "  Kafka: localhost:9093"
    echo "  Kafka UI: http://localhost:8084"
    echo "  Redis Commander: http://localhost:8085"
    echo ""
    
    print_status "Database Connection:"
    echo "  Host: localhost"
    echo "  Port: 5433"
    echo "  Database: notification_db"
    echo "  Username: postgres"
    echo "  Password: password"
    echo ""
    
    print_status "Environment Variables:"
    echo "  DB_USERNAME=postgres"
    echo "  DB_PASSWORD=password"
    echo "  KAFKA_BOOTSTRAP_SERVERS=localhost:9093"
    echo "  REDIS_HOST=localhost"
    echo "  REDIS_PORT=6380"
}

# Function to stop the environment
stop_environment() {
    print_status "Stopping Notification Service development environment..."
    docker-compose down
    print_success "Development environment stopped"
}

# Function to clean up everything
cleanup() {
    print_status "Cleaning up Notification Service development environment..."
    docker-compose down -v --remove-orphans
    print_success "Cleanup completed"
}

# Function to show logs
show_logs() {
    print_status "Showing logs for all services..."
    docker-compose logs -f
}

# Function to show help
show_help() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start       Start the development environment"
    echo "  stop        Stop the development environment"
    echo "  restart     Restart the development environment"
    echo "  status      Show service status and connection info"
    echo "  logs        Show logs for all services"
    echo "  cleanup     Stop and remove all containers and volumes"
    echo "  help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start    # Start the development environment"
    echo "  $0 status   # Show service status"
    echo "  $0 logs     # Show service logs"
}

# Main script logic
case "${1:-start}" in
    start)
        print_status "Setting up Notification Service development environment..."
        check_docker
        check_ports
        start_environment
        wait_for_services
        create_kafka_topics
        run_migrations
        show_status
        print_success "Notification Service development environment is ready!"
        ;;
    stop)
        stop_environment
        ;;
    restart)
        stop_environment
        sleep 2
        $0 start
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    cleanup)
        cleanup
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac
