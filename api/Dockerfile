# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src src/
RUN mvn clean install

# Stage 2: Create the production image
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/target/api-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
