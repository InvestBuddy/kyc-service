# Use JDK 21 as the base image
FROM openjdk:21-jdk

# Set working directory
WORKDIR /app

# Copy the JAR file and rename it to notification.jar
COPY target/kyc-service-1.0-SNAPSHOT kyc-service.jar

# Expose port
EXPOSE 8084

# Run the application
ENTRYPOINT ["java", "-jar", "kyc-service.jar"]
