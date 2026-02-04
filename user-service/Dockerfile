# Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Required for 'service_healthy' status checks in Docker Compose
RUN apk add --no-cache wget

# Standard non-root security setup
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy User Service artifact from your LOCAL target folder
COPY target/user-service-*.jar app.jar

# User Service Port
EXPOSE 8081

# Performance: Optimized for container memory limits
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]