# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy gradle wrapper and related files for caching layer
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make wrapper executable
RUN chmod +x ./gradlew

# Download dependencies (cached as a layer unless build.gradle/settings.gradle changes)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code and build the application (skip tests for faster build)
COPY src src
RUN ./gradlew build -x test --no-daemon

# Stage 2: Runtime environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user for security ('spring' user in 'spring' group)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
