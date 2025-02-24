FROM openjdk:17.0.1-jdk-slim
VOLUME /tmp
COPY ./28delivery-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]