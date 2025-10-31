FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/payment-service-*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]


