# Course Management Web Application

This application is a comprehensive course management system built with Spring Boot. It provides features for course management, user administration, and educational resource sharing.

## Prerequisites

Before you begin, ensure you have the following installed:

- [Java JDK 11+](https://adoptopenjdk.net/)
- [Gradle](https://gradle.org/install/) (or use the included Gradle wrapper)
- Web browser (Chrome, Firefox, etc.)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/AKAshrimp/Course-web-app.git
cd Course-web-app
```

### Build and Run the Application

You can build and run the application using the Gradle wrapper:

```bash
./gradlew bootRun
```

For Windows users:

```bash
gradlew.bat bootRun
```

The application will start and be available at [http://localhost:8080](http://localhost:8080)

## Database

This application uses an H2 in-memory database for development purposes.

### H2 Console

You can access the H2 database console at [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

Use the following settings to log in:
- JDBC URL: `jdbc:h2:file:./coursewebsite_db`
- Username: `sa`
- Password: `password`

## Features

- **Course Management**: Create, update, and organize courses
- **User Management**: Admin functions for user management
- **Material Uploads**: Upload and manage course materials
- **Internationalization**: Supports multiple languages

## Project Structure

- `src/main/java/com/example/coursewebsite/` - Contains Java source code
- `src/main/resources/` - Contains configuration files and static resources
- `src/main/webapp/` - Contains JSP views

## Configuration

Application settings can be found in `src/main/resources/application.properties`

## License

This project is licensed under the MIT License - see the LICENSE file for details. 