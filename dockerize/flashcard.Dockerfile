FROM openjdk:11
WORKDIR /app/
COPY flashcard-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "flashcard-service-0.0.1-SNAPSHOT.jar"]
EXPOSE 8089
