
# Stage 1: Spring Boot application
FROM openjdk:17-alpine

EXPOSE 9090
EXPOSE 3306

COPY target/baloot7-0.0.1-SNAPSHOT.jar app.jar


ENTRYPOINT ["java","-jar","/app.jar"]

