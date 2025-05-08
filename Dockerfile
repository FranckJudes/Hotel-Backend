
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17
COPY --from=build /app/target/backend-hotel.jar backend-hotel.jar
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-jar", "/backend-hotel.jar"]