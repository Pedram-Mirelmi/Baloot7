FROM maven:3.8.5-openjdk-17
WORKDIR /baloot
COPY pom.xml /baloot
CMD mvn dependency:resolve
COPY src /baloot/src
RUN mvn clean package
CMD mvn spring-boot:run
EXPOSE 9090
ENTRYPOINT ["java","-jar","/baloot/target/baloot7-0.0.1-SNAPSHOT.jar"]
