# Travel Booking System - Backend

A modern Spring Boot REST API for a complete travel booking system with JWT authentication, PostgreSQL database, and comprehensive testing.

## Features

- **User Management**: Registration, authentication, and JWT-based security
- **User Profiles**: Complete profile management with personal information
- **Travel Packages**: CRUD operations for travel packages with pricing
- **Booking System**: Full booking workflow with confirmation
- **RESTful API**: Clean REST endpoints with proper HTTP status codes
- **Database Integration**: PostgreSQL with JPA/Hibernate
- **Security**: Spring Security with JWT tokens
- **Testing**: Comprehensive unit and integration tests
- **Documentation**: Swagger/OpenAPI documentation
- **Docker Support**: Containerized deployment

## Technology Stack

- **Java 17**
- **Spring Boot 3.1**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL 15**
- **JWT (JSON Web Tokens)**
- **Maven**
- **JUnit 5**
- **Testcontainers**
- **Swagger/OpenAPI 3**
- **Docker**

## Prerequisites

- Java 17 or later
- Maven 3.8+
- PostgreSQL 15+ (or Docker)
- Docker & Docker Compose (for containerized setup)

## Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/zubairansari-codes/travel-booking-system-fullstack.git
cd travel-booking-system-fullstack/backend

# Start the application with Docker Compose
docker-compose up -d

# The API will be available at http://localhost:8080
```

### Option 2: Local Development Setup

1. **Setup PostgreSQL Database**
```bash
# Using Docker (recommended)
docker run --name travel-postgres \
  -e POSTGRES_DB=travel_booking \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15

# Or install PostgreSQL locally and create database
psql -U postgres -c "CREATE DATABASE travel_booking;"
```

2. **Configure Application Properties**
```bash
# Copy the example properties
cp src/main/resources/application-example.properties src/main/resources/application.properties

# Edit the database configuration
vim src/main/resources/application.properties
```

3. **Build and Run**
```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run

# Or build JAR and run
mvn clean package
java -jar target/travel-booking-system-0.0.1-SNAPSHOT.jar
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host | localhost |
| `DB_PORT` | PostgreSQL port | 5432 |
| `DB_NAME` | Database name | travel_booking |
| `DB_USERNAME` | Database username | postgres |
| `DB_PASSWORD` | Database password | password |
| `JWT_SECRET` | JWT signing secret | your-secret-key |
| `JWT_EXPIRATION` | JWT expiration time (ms) | 86400000 (24h) |
| `SERVER_PORT` | Application port | 8080 |

### Application Properties

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/travel_booking
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT Configuration
app.jwt.secret=your-secret-key
app.jwt.expiration=86400000

# Server Configuration
server.port=8080

# Swagger Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## API Documentation

### Swagger UI
Once the application is running, access the interactive API documentation:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Main Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/logout` - Logout user

#### User Management
- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/profile` - Update user profile
- `GET /api/users/{id}` - Get user by ID (Admin)
- `GET /api/users` - List all users (Admin)

#### Travel Packages
- `GET /api/packages` - List all packages
- `GET /api/packages/{id}` - Get package details
- `POST /api/packages` - Create new package (Admin)
- `PUT /api/packages/{id}` - Update package (Admin)
- `DELETE /api/packages/{id}` - Delete package (Admin)

#### Bookings
- `GET /api/bookings` - Get user bookings
- `GET /api/bookings/{id}` - Get booking details
- `POST /api/bookings` - Create new booking
- `PUT /api/bookings/{id}/cancel` - Cancel booking
- `GET /api/admin/bookings` - List all bookings (Admin)

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report

# Run only unit tests
mvn test -Dtest="*Test"

# Run only integration tests
mvn test -Dtest="*IT"

# Run specific test class
mvn test -Dtest="UserServiceTest"
```

### Test Structure

```
src/test/java/com/zubair/travel/
├── integration/          # Integration tests
│   ├── AuthControllerIT.java
│   ├── UserControllerIT.java
│   └── BookingControllerIT.java
├── unit/                 # Unit tests
│   ├── service/
│   │   ├── UserServiceTest.java
│   │   ├── AuthServiceTest.java
│   │   └── BookingServiceTest.java
│   └── controller/
│       ├── UserControllerTest.java
│       └── BookingControllerTest.java
└── TravelBookingApplicationTests.java
```

### Test Database
Integration tests use Testcontainers with PostgreSQL for realistic testing.

## Docker Support

### Building Docker Image

```bash
# Build the Docker image
docker build -t travel-booking-backend .

# Run the container
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PASSWORD=your-password \
  travel-booking-backend
```

### Docker Compose

The included `docker-compose.yml` provides a complete development environment:

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Reset database
docker-compose down -v && docker-compose up -d
```

### Services Included
- **app**: Spring Boot application (port 8080)
- **db**: PostgreSQL database (port 5432)
- **adminer**: Database admin interface (port 8081)

## Development Workflow

### Code Style
- Follow Java coding conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods small and focused

### Database Migrations
```bash
# Application uses Hibernate DDL auto-update
# For production, use Flyway migrations:

# Add Flyway dependency to pom.xml
# Create migration files in src/main/resources/db/migration/
# Example: V1__Create_user_table.sql
```

### Adding New Features

1. **Create Feature Branch**
```bash
git checkout -b feature/new-feature-name
```

2. **Write Tests First (TDD)**
```bash
# Create test class
# Write failing test
# Implement feature
# Ensure tests pass
mvn test
```

3. **Update Documentation**
- Update this README if needed
- Add/update API documentation
- Update Swagger annotations

## Deployment

### Production Build

```bash
# Create production build
mvn clean package -Pprod

# The JAR file will be in target/
ls -la target/*.jar
```

### Environment-Specific Profiles

- `application.properties` - Default configuration
- `application-dev.properties` - Development profile
- `application-prod.properties` - Production profile
- `application-test.properties` - Test profile

```bash
# Run with specific profile
java -jar target/app.jar --spring.profiles.active=prod
```

### Health Checks

```bash
# Application health
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info
```

## Monitoring and Logging

### Application Logs
```bash
# View application logs
tail -f logs/application.log

# In Docker
docker-compose logs -f app
```

### Performance Monitoring
- Spring Boot Actuator endpoints available at `/actuator/*`
- Metrics available at `/actuator/metrics`
- Health check at `/actuator/health`

## Troubleshooting

### Common Issues

**Database Connection Issues**
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check connectivity
telnet localhost 5432

# Verify database exists
psql -U postgres -l
```

**JWT Token Issues**
```bash
# Verify JWT secret is set
echo $JWT_SECRET

# Check token expiration in application logs
```

**Port Already in Use**
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Debug Mode

```bash
# Run with debug logging
mvn spring-boot:run -Dspring.profiles.active=dev -Dlogging.level.com.zubair=DEBUG

# Remote debugging
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
```

## API Integration Examples

### JavaScript/React Integration

```javascript
// Authentication
const login = async (email, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, password }),
  });
  
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
};

// Authenticated API call
const fetchPackages = async () => {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/packages', {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });
  
  return await response.json();
};
```

### cURL Examples

```bash
# Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'

# Get packages (authenticated)
curl -X GET http://localhost:8080/api/packages \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create booking
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "packageId": 1,
    "travelDate": "2024-06-15",
    "numberOfTravelers": 2
  }'
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.

## Support

For support and questions:
- Create an issue on GitHub
- Email: zubairansari.codes@gmail.com
- Documentation: Check the `/docs` folder for additional documentation

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history and updates.
