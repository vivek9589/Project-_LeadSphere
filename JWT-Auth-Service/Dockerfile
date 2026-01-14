# Stage 1: Build Stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the JAR from the build stage
# ArtifactId is JWT-Auth-Service
COPY --from=build /app/target/JWT-Auth-Service-0.0.1-SNAPSHOT.jar app.jar

# Auth Service usually runs on 8084
EXPOSE 8084

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]