FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/order-service-*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
