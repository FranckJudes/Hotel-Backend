
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17
VOLUME /tmp
COPY --from=build /app/target/backend-hotel.jar backend-hotel.jar
ENTRYPOINT [ "java","-jar","/backend-hotel.jar" ]