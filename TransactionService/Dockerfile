# Stage 1: Build the Spring Boot jar
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
# Copy only pom.xml first to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline
# Copy the rest of the project
COPY src ./src
# Build the jar (skip tests to speed up)
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/app.jar app.jar
EXPOSE 8083
# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
