# Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install wget for health checks
RUN apk add --no-cache wget

# Security: Run as non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the Gateway JAR from your local target folder
# Note: Ensure the filename pattern matches your actual build output
COPY target/Gateway-*.jar app.jar

# Gateway standard port
EXPOSE 8080

# Run with container-aware memory limits
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]