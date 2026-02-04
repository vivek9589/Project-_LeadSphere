# Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Required for 'service_healthy' status checks in Docker Compose
RUN apk add --no-cache wget

# Standard non-root security setup
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy Auth Service artifact from your LOCAL target folder
COPY target/jwt-auth-service-*.jar app.jar

# Auth Service Port
EXPOSE 8084

# Optimized for security and memory efficiency
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]