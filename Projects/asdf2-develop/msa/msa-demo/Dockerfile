FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/msa-demo-*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
