FROM openjdk:16-jdk-alpine

WORKDIR auth-api

ENV TERM xterm-256color
ENTRYPOINT ["java", "-XX:+ShowCodeDetailsInExceptionMessages", "-jar", "app.jar"]

ADD docker/deployment/api/application.yml /auth-api/application.yml
ADD api/target/hangar-auth-*.jar /auth-api/app.jar
