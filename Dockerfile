# ----------------------------------------------
# STAGE 1: BUILD THE APPLICATION (The Builder Image)
# ----------------------------------------------
# Use Temurin's JDK for compiling
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy Maven files... (rest of Stage 1 remains the same)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src src

# Build the JAR file
RUN ./mvnw install -DskipTests

# ----------------------------------------------
# STAGE 2: CREATE THE FINAL RUNTIME IMAGE (The Execution Image)
# ----------------------------------------------
# Use Temurin's JRE-only image for the final container
FROM eclipse-temurin:17-jre-jammy
# Everything below remains the same
WORKDIR /usr/app
ENV PORT 8080
COPY --from=build /app/target/*.jar app.jar
EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]