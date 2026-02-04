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

# Required for 'service_healthy' status checks in Docker Compose
RUN apk add --no-cache wget

# Standard non-root security setup
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy Auth Service artifact
COPY --from=build /app/target/JWT-Auth-Service-*.jar app.jar

EXPOSE 8084

# Optimized for security and memory efficiency
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]