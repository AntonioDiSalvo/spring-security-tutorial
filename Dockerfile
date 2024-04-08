FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/social-0.0.1-SNAPSHOT.jar /app/social-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "social-0.0.1-SNAPSHOT.jar"]
