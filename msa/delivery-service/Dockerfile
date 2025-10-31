FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/delivery-service-*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
