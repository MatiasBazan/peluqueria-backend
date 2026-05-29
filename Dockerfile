# --- Stage 1: Build the application ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 2: Run the application ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/peluqueria-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
