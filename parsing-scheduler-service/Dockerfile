FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY target/parsing-scheduler-service*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
