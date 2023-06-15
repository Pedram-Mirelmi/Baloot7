# Stage 1: Spring Boot application
#FROM openjdk:17-alpine
#VOLUME /tmp
#COPY target/baloot7-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]


#FROM maven:3.8.3-openjdk-11-slim AS builder
#WORKDIR /app
#COPY pom.xml .
#RUN mvn dependency:go-offline
#COPY src ./src
#RUN mvn package -DskipTests
#
## Stage 2
#FROM openjdk:11-jre-slim
#WORKDIR /app
#COPY --from=builder /app/target/baloot7-0.0.1-SNAPSHOT.jar .
#COPY application.properties .
#RUN apt-get update && apt-get install -y mysql-client
#EXPOSE 8080
#CMD ["java", "-jar", "your-application.jar"]

# Use the official OpenJDK 11 image as the base image
FROM openjdk:17-alpine

# Install MySQL client
RUN apk update && \
    apk add mysql mysql-client && \
    rm -rf /var/cache/apk/*

# Set environment variables for MySQL connection
ENV MYSQL_HOST=localhost
ENV MYSQL_PORT=3306
ENV MYSQL_DATABASE=Baloot7
ENV MYSQL_USER=root
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_PASSWORD=root

COPY ./docker/startup.sh /startup.sh
COPY ./docker/mysql.conf /etc/mysql/my.cnf

# Copy the Spring Boot application JAR file to the container
COPY target/baloot7-0.0.1-SNAPSHOT.jar /app.jar

# Expose the port on which the Spring Boot application will listen
EXPOSE 9090
EXPOSE 3306

CMD ["/startup.sh"]

# Run the Spring Boot application with MySQL
CMD ["java", "-jar", "app.jar"]

