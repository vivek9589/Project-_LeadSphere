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

# Ensure Alpine has wget for health monitoring
RUN apk add --no-cache wget

# Security: Non-root execution
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy Analytics Service artifact
COPY --from=build /app/target/analytics-service-*.jar app.jar

EXPOSE 8085

# Optimized for high-throughput virtual threads processing
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]