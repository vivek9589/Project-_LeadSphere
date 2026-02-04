# Stage: Runtime
# We use eclipse-temurin because it has excellent native support for both AMD64 and ARM64
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 1. Install wget (useful for Docker health checks later)
RUN apk add --no-cache wget

# 2. Security: Create a non-root user so the app doesn't run as 'root' in the cloud
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 3. Copy the JAR from your local 'target' folder (built on your Windows machine)
# The asterisk (*) ensures it finds the versioned JAR file
COPY target/eureka-server-*.jar app.jar

# 4. Open the port for the service
EXPOSE 8761

# 5. Run the application with optimized memory settings for containers
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]