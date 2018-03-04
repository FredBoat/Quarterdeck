FROM openjdk:9-jdk-slim

ENV ENV docker

RUN mkdir -p /opt/Backend

COPY backend.example.yaml /opt/Backend/backend.yaml
COPY Backend.jar /opt/Backend/Backend.jar

EXPOSE 1356

WORKDIR /opt/Backend
ENTRYPOINT ["java", "-Xmx64m", "-jar", "Backend.jar"]
