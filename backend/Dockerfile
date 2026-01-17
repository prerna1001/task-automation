FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Gradle wrapper and project files
COPY . .

# Build the Spring Boot fat jar
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
