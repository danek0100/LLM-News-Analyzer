FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY target/news-parser-service*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
