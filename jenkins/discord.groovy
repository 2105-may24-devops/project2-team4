#!/usr/bin/env groovy

def 


def sendDiscordMessage() {
    discordSend description: "Node Name: $NODE_NAME \n Job Name: $JOB_NAME \n Build Number: $BUILD_NUMBER \n Build ID: $BUILD_ID", 
        footer: 'something here.', 
        image: '', 
        link: "http://52.142.60.104:8080/jenkins/job/project2-team4/job/$BRANCH_NAME/", 
        result: "$currentBuild.currentResult", 
        thumbnail: 'https://www.jenkins.io/images/logos/pixelart/jenkins-pixelart-32.png', 
        title: 'Team4-P2',
        // create env
        webhookURL: ${env.discord_webhook}
}

return this