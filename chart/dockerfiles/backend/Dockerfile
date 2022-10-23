FROM eclipse-temurin:17-jre-alpine

RUN addgroup -g 1000 java \
  && adduser -Ss /bin/false -u 1000 -G java -h /home/java java

USER java
WORKDIR auth-backend

ENV TERM xterm-256color
ENTRYPOINT ["java", "-jar", "app.jar"]

ADD backend/target/hangar-auth-*.jar /auth-backend/app.jar
