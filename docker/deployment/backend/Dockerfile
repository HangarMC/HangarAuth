FROM eclipse-temurin:17-jre-alpine

WORKDIR auth-backend

ENV TERM xterm-256color
ENTRYPOINT ["java", "-jar", "app.jar"]

ADD docker/deployment/backend/application.yml /auth-backend/application.yml
ADD backend/target/hangar-auth-*.jar /auth-backend/app.jar
