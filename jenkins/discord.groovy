#!/usr/bin/env groovy

def sendDiscordMessage() {
    discordSend description: "Node Name: $NODE_NAME \n Job Name: $JOB_NAME \n Build Number: $BUILD_NUMBER \n Build ID: $BUILD_ID", 
        footer: 'something here.', 
        image: '', 
        link: 'http://52.142.60.104:8080/jenkins/job/project2-team4/job/master/', 
        result: "$currentBuild.currentResult", 
        thumbnail: 'https://www.jenkins.io/images/logos/pixelart/jenkins-pixelart-32.png', 
        title: 'Team4-P2', 
        webhookURL: 'https://discord.com/api/webhooks/856587514218545203/OmVq23OAAKWh6VQGfoiFE-DRlGwtDst19OLHedR9HaQ7eONnVdlBJlc1i8OiAUpz4hqd'
}

return this