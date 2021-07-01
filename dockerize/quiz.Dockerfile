FROM openjdk:11
WORKDIR /app/
COPY  quiz-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "quiz-service-0.0.1-SNAPSHOT.jar"]
EXPOSE 8090
