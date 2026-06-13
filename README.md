# Course Web App

A Spring Boot course management web application with JSP pages, role-based access, lecture materials, polls, REST APIs, JWT admin account management, a React Admin frontend, Swagger UI, Flyway migrations, WebSocket poll updates, Actuator health checks, Docker support, and GitHub Actions CI.

## Tech Stack

- Java 17
- Spring Boot 3.4
- Spring MVC + JSP
- Spring Security
- Spring OAuth2 Resource Server / JWT
- Spring Data JPA / Hibernate
- H2 for local development
- MySQL for production profile
- Flyway
- React + Vite + Ant Design
- JUnit 5 + MockMvc
- Docker / Docker Compose

## Features

- Student and teacher accounts with role-based access
- Lecture creation and material uploads
- Poll creation, voting, vote updates, and vote history
- Bean Validation for form input
- REST API under `/api/v1`
- JWT-protected admin API under `/api/admin`
- Independent React Admin frontend under `admin-frontend`
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
| React Admin | `http://localhost:5173` |

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

## Admin Account Management

The project includes a separate React Admin app for teacher-only account management.

Backend admin endpoints:

| Endpoint | Purpose |
| --- | --- |
| `POST /api/admin/auth/login` | Login with username/password and receive a JWT |
| `GET /api/admin/users` | List users |
| `GET /api/admin/users/{id}` | Get one user |
| `POST /api/admin/users` | Create user |
| `PUT /api/admin/users/{id}` | Update user profile and roles |
| `PUT /api/admin/users/{id}/password` | Update password |
| `DELETE /api/admin/users/{id}` | Delete user |

Only users with `ROLE_TEACHER` can access the admin user APIs.

Run the admin frontend:

```bash
cd admin-frontend
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

Default admin login:

```text
username: teacher
password: password
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

Copy `.env.example` to `.env` first:

```bash
cp .env.example .env
```

Then run:

```bash
docker compose up --build
```

This starts the app with the `prod` profile and a MySQL 8 database.

If port `3306` is already used on your computer, Docker exposes MySQL on host port `3307` by default:

```text
localhost:3307 -> container mysql:3306
```

To start only MySQL:

```bash
docker compose up -d db
```

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
JWT_SECRET
ADMIN_ORIGIN
MYSQL_HOST_PORT
```

Example for running Spring Boot locally against Docker MySQL:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:DB_HOST="localhost"
$env:DB_PORT="3307"
$env:DB_NAME="coursewebsite"
$env:DB_USERNAME="appuser"
$env:DB_PASSWORD="apppassword"
$env:JWT_SECRET="replace-with-at-least-32-character-secret"
.\gradlew.bat bootRun
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

Frontend structure:

```text
admin-frontend
├── src/api
├── src/auth
├── src/layout
└── src/pages
```