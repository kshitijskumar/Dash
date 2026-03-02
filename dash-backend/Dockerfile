# Multi-stage Dockerfile for Ktor Backend
# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper files
COPY gradlew ./
COPY gradlew.bat ./
COPY gradle ./gradle

# Copy gradle configuration files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached if gradle files don't change)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew buildFatJar --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/build/libs/*-all.jar app.jar

# Expose port 8080 (documentation only - Render will use PORT env var)
EXPOSE 8080

# Set environment variables (can be overridden by Render)
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV PORT=8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
