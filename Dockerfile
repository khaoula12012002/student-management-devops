# ====== Build Stage ======
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy the Maven build output into the image
COPY target/student-management-0.0.1-SNAPSHOT.jar app.jar

# ====== Runtime Stage ======
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
