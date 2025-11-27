# Multi-stage build for Spring Boot application

# Stage 1: Build the application
# FROM maven:3.9-eclipse-temurin-17-alpine AS build
# WORKDIR /app

# Copy pom.xml and download dependencies (layer caching)
# COPY pom.xml .
# COPY mvnw .
# COPY .mvn .mvn
# RUN mvn dependency:go-offline -B

# Copy source code and build
# COPY src ./src
# RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine
# WORKDIR /app

# Create a non-root user for security
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring

# Copy the built jar from the build stage
COPY target/student-management-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8089

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
