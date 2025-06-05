# NewsBucket API Gateway

This is the API Gateway service for the NewsBucket application, built with Spring Cloud Gateway. It serves as the entry point for all client requests, routing them to the appropriate backend services.

## Features

### Core Gateway Features
- **Intelligent Routing**: Routes requests to appropriate backend services based on path patterns
- **Cross-Origin Resource Sharing (CORS)**: Configured to allow cross-origin requests
- **Circuit Breaker**: Implements circuit breaker pattern using Resilience4j to handle service failures gracefully
- **Fallback Responses**: Provides fallback responses when services are unavailable
- **Request/Response Logging**: Logs all incoming requests and outgoing responses

### Advanced Features
- **Rate Limiting**: Limits the number of requests a client can make in a given time period using Redis
- **Retry Mechanism**: Automatically retries failed requests to improve resilience
- **Metrics & Monitoring**: Exposes metrics via Micrometer and Prometheus for monitoring
- **Health Checks**: Provides health check endpoints for monitoring service health
- **Embedded Redis**: Includes an embedded Redis server for development purposes

## Configuration

### Routing Rules

- `/api/news/**` → Routes to the News Service (http://localhost:8081)
- `/**` → Routes to the Frontend Client (http://localhost:5173)
- `/actuator/**` → Routes to the Gateway's own Actuator endpoints

### Rate Limiting

The API Gateway implements rate limiting using Redis:
- Default limit: 10 requests per second
- Burst capacity: 20 requests
- Key resolver: Client IP address

### Circuit Breaker

Circuit breaker configuration:
- Failure rate threshold: 50%
- Minimum number of calls: 5
- Wait duration in open state: 30 seconds

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven

### Build and Run

```bash
# Build the application
mvn clean package

# Run the application
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

Alternatively, use the provided run script:
```bash
./run.bat  # Windows
```

## Monitoring

The API Gateway exposes various endpoints for monitoring:

- Health check: `/health`
- Actuator endpoints: `/actuator`
- Prometheus metrics: `/actuator/prometheus`

## Dependencies

- Spring Boot 3.2.0
- Spring Cloud Gateway
- Spring Cloud Circuit Breaker with Resilience4j
- Spring Data Redis Reactive
- Micrometer with Prometheus Registry
- Embedded Redis (for development)