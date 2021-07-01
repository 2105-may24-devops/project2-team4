#! /bin/bash
rm ./dockerize/*.jar
mvn clean package -f ./flashcard-service/
mvn clean package -f ./quiz-service/
mvn clean package -f ./gateway-service/
cp ./flashcard-service/target/flashcard-service-0.0.1-SNAPSHOT.jar dockerize/
cp ./quiz-service/target/quiz-service-0.0.1-SNAPSHOT.jar dockerize/
cp gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar ./dockerize/
