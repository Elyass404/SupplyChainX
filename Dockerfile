# ----------------------------------------------
# STAGE 1: BUILD THE APPLICATION (The Builder Image)
# ----------------------------------------------
# Use a full JDK image for compiling the code
FROM openjdk:17-jdk-slim AS build
WORKDIR /app

# Copy Maven files first. This layer rarely changes, speeding up subsequent builds.
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
# Download all dependencies without building the full app yet
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src src

# Build the JAR file. We skip tests here because they will run in the CI/CD pipeline.
RUN ./mvnw install -DskipTests

# ----------------------------------------------
# STAGE 2: CREATE THE FINAL RUNTIME IMAGE (The Execution Image)
# ----------------------------------------------
# Use a lightweight JRE-only image for the final production container
FROM openjdk:17-jre-slim

# Set up the container environment
WORKDIR /usr/app
ENV PORT 8080

# Copy the built JAR from the 'build' stage
# This copies the artifact from the first stage into this minimal image
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot application runs on
EXPOSE ${PORT}

# Define the command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]