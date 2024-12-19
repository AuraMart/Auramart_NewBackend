# Start with the official OpenJDK 17 image as the base image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Maven-built JAR file into the container
COPY target/aura-mart-0.0.1-SNAPSHOT.jar /app/aura-mart-0.0.1-SNAPSHOT.jar

# Expose the port your Spring Boot application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/aura-mart-0.0.1-SNAPSHOT.jar"]
