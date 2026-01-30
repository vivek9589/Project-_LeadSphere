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

# Add wget for health monitoring
RUN apk add --no-cache wget

# Security: Spring user context
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Specified artifact name to prevent wildcard conflicts with sources/javadoc JARs
COPY --from=build /app/target/user-service-*.jar app.jar

EXPOSE 8081

# JVM tuning for Java 21
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]