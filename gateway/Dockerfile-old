# --- Stage 1: Build Stage ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Ensure wget is present for health checks (Checking /actuator/health)
RUN apk add --no-cache wget

# Security: Avoid running as root in production
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy Gateway JAR (Case-sensitive artifact ID)
COPY --from=build /app/target/Gateway-*.jar app.jar

EXPOSE 8080

# Use Virtual Thread awareness if enabled in application.properties
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]