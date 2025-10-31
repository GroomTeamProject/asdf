FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/owner-service-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
