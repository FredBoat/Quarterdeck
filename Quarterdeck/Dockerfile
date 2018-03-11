FROM openjdk:9-jdk-slim

ENV ENV docker

RUN mkdir -p /opt/Quarterdeck

COPY quarterdeck.example.yaml /opt/Quarterdeck/quarterdeck.yaml
COPY Quarterdeck.jar /opt/Quarterdeck/Quarterdeck.jar

EXPOSE 1356

WORKDIR /opt/Quarterdeck
ENTRYPOINT ["java", "-Xmx64m", "-jar", "Quarterdeck.jar"]
