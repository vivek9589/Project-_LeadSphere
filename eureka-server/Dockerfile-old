# --- Stage 1: Build Stage (The Kitchen) ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache dependencies to speed up subsequent builds
COPY pom.xml .
RUN mvn dependency:go-offline

# Package the Eureka application
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime Stage (The Serving Table) ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install wget for Docker health check support
RUN apk add --no-cache wget

# Security: Run as a non-privileged user named 'spring'
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built JAR from the build stage
COPY --from=build /app/target/eureka-server-*.jar app.jar

# Eureka Default Port
EXPOSE 8761

# Performance: JVM flags optimized for container memory limits
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]