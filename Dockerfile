# Multi-stage build for Spring Boot course website

FROM gradle:8.5-jdk17 AS builder
WORKDIR /workspace
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd -ms /bin/bash spring && \
    mkdir -p /app/uploads && \
    chown -R spring:spring /app
USER spring
COPY --from=builder /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","/app/app.jar"]
