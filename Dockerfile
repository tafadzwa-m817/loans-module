# Build stage
FROM maven:3.8.4-openjdk-17 AS build

COPY . .
RUN mvn clean install -DskipTests

# Final stage
FROM openjdk:17

# Copy the jar file from the build stage only
COPY --from=build app/target/zdf-loans-module.jar .

# Expose the port
EXPOSE 7200

CMD ["java", "-jar","zdf-loans-module.jar"]
