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

# Add wget for Docker Compose health check
RUN apk add --no-cache wget

# Security best practice
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy Lead Service JAR
COPY --from=build /app/target/lead-service-*.jar app.jar

EXPOSE 8082

# Performance: Optimal memory allocation within the container
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]