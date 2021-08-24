FROM openjdk:16-jdk-alpine

WORKDIR paperauth

ENV TERM xterm-256color

EXPOSE 8080
# "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" is for debug only
ENTRYPOINT ["java", "-XX:+ShowCodeDetailsInExceptionMessages", "-jar", "app.jar"]

ADD docker/application.yml /paperauth/application.yml
ADD target/paperauth-*.jar /paperauth/app.jar
