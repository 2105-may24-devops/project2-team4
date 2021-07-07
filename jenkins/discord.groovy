#!/usr/bin/env groovy

def createDescription(dockerChanges, serviceChanges) {
    // both dockerChanges and serviceChanges must have the same keys 
    def description = "Node Name: $NODE_NAME \nJob Name: $JOB_NAME \nBuild Number: $BUILD_NUMBER\n Build ID: $BUILD_ID \n"

    for (key in dockerChanges.keySet()) {
        description = "${description}${key}: ${ dockerChanges[key][1] || serviceChanges[key] ? 'updated' : 'unchanged' } \n"
    }
    return description
}


def sendDiscordMessage(msg) {
    discordSend description: "$msg", 
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