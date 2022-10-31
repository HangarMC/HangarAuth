FROM eclipse-temurin:17.0.4_8-jre-alpine

WORKDIR auth-backend

ENV TERM xterm-256color
ENTRYPOINT ["java", "-jar", "app.jar"]

ADD backend/target/hangar-auth-*.jar /auth-backend/app.jar
