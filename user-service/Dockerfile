# Stage 1: The "Kitchen" (Build Stage)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy only the shopping list first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the actual code and cook the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: The "Serving Table" (Runtime Stage)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Take only the finished dish (JAR) from the kitchen
COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar

# The port the service lives on
EXPOSE 8081

# Serve the application
ENTRYPOINT ["java", "-jar", "app.jar"]