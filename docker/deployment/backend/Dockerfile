FROM openjdk:17-jdk-alpine

WORKDIR auth-backend

ENV TERM xterm-256color
ENTRYPOINT ["java", "-XX:+ShowCodeDetailsInExceptionMessages", "-jar", "app.jar"]

ADD docker/deployment/api/application.yml /auth-backend/application.yml
ADD backend/target/hangar-auth-*.jar /auth-backend/app.jar
