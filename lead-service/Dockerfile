# Stage 1: Build Stage (The Kitchen)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the pom.xml to download dependencies separately (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the JAR file
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime Stage (The Serving Table)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the JAR from the build stage
# Note: The name matches the artifactId 'lead-service' from your pom
COPY --from=build /app/target/lead-service-*.jar app.jar

# Expose the port (Lead-service usually runs on 8082)
EXPOSE 8082

# Start the service
ENTRYPOINT ["java", "-jar", "app.jar"]