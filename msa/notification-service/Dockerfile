FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/notification-service-*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]
