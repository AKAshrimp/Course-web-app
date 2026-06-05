# Course Web App

A Spring Boot course management web application with JSP pages, role-based access, lecture materials, polls, REST APIs, Swagger UI, Flyway migrations, WebSocket poll updates, Actuator health checks, Docker support, and GitHub Actions CI.

## Tech Stack

- Java 17
- Spring Boot 3.4
- Spring MVC + JSP
- Spring Security
- Spring Data JPA / Hibernate
- H2 for local development
- MySQL for production profile
- Flyway
- JUnit 5 + MockMvc
- Docker / Docker Compose

## Features

- Student and teacher accounts with role-based access
- Lecture creation and material uploads
- Poll creation, voting, vote updates, and vote history
- Bean Validation for form input
- REST API under `/api/v1`
- Swagger UI at `/swagger-ui.html`
- WebSocket poll updates through `/ws` and `/topic/poll/{pollId}`
- Actuator health and metrics endpoints
- CI build on push and pull request

## Run Locally

Windows:

```bash
gradlew.bat bootRun
```

macOS/Linux:

```bash
./gradlew bootRun
```

Open:

```text
http://localhost:8080
```

Default users are seeded on first startup:

| Role | Username | Password |
| --- | --- | --- |
| Student | `student` | `password` |
| Teacher | `teacher` | `password` |

## Useful URLs

| Feature | URL |
| --- | --- |
| App | `http://localhost:8080` |
| H2 console | `http://localhost:8080/h2-console` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Health check | `http://localhost:8080/actuator/health` |
| Metrics | `http://localhost:8080/actuator/metrics` |

H2 settings:

```text
JDBC URL: jdbc:h2:file:./coursewebsite_db
Username: sa
Password: password
```

## REST API Examples

```bash
curl http://localhost:8080/api/v1/lectures
curl http://localhost:8080/api/v1/polls
```

Authenticated poll voting is available at:

```text
POST /api/v1/polls/{pollId}/vote
```

Request body:

```json
{
  "optionId": 1
}
```

## Run Tests

```bash
gradlew.bat test
```

Full build:

```bash
gradlew.bat build
```

## Run with Docker Compose

Create a local `.env` file first:

```text
MYSQL_ROOT_PASSWORD=change-me-root
DB_NAME=coursewebsite
DB_USERNAME=appuser
DB_PASSWORD=change-me-app
DB_PORT=3306
```

Then run:

```bash
docker compose up --build
```

This starts the app with the `prod` profile and a MySQL 8 database.

## Production Profile

The production profile uses MySQL and Flyway:

```bash
gradlew.bat bootRun --args="--spring.profiles.active=prod"
```

Required environment variables can override defaults:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
```

## Project Structure

```text
src/main/java/com/example/coursewebsite
├── config
├── controller
├── dto
├── model
├── repository
└── service
```

```text
src/main/resources/db/migration
```

contains Flyway database migrations.