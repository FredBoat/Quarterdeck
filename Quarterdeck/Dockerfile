FROM openjdk:10-jdk-slim

ENV ENV docker

WORKDIR /opt/Quarterdeck

EXPOSE 4269

ENTRYPOINT ["java", "-Xmx128m", "-jar", "Quarterdeck.jar"]

COPY quarterdeck.example.yaml /opt/Quarterdeck/quarterdeck.yaml
COPY Quarterdeck.jar /opt/Quarterdeck/Quarterdeck.jar
