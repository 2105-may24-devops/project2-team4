FROM openjdk:11
WORKDIR /app/
COPY gateway-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "gateway-service-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
