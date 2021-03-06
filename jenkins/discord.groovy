#!/usr/bin/env groovy
@NonCPS
def capitalizeServiceName(serviceName) {
    def split = serviceName.split("-")
    return "${split[0].charAt(0).toUpperCase()}${split[0].substring(1)} ${split[1].charAt(0).toUpperCase()}${split[1].substring(1)}"
}

def createDescription(dockerChanges, serviceChanges, postmanResults, deployStatus) {
    // both dockerChanges and serviceChanges must have the same keys 
    def description = "Node Name: $NODE_NAME \nJob Name: $JOB_NAME \nBuild ID: $BUILD_ID \n"

    for (key in dockerChanges.keySet()) {
        // capitalize service names
        def serviceName = capitalizeServiceName(key)
        description = "${description}${serviceName}: ${ dockerChanges[key][1] || serviceChanges[key] ? 'Updated' : 'Unchanged' } \n"
    }
    description = "$description Postman Tests: ${ postmanResults ? 'Passed' : 'Failed' } \n Deployment: $deployStatus"
    return description
}


def sendDiscordMessage(description, footer) {
    discordSend description: description, 
        footer: footer,
        image: '', 
        link: "http://52.142.60.104:8080/jenkins/job/project2-team4/job/$BRANCH_NAME/$BUILD_ID", 
        result: "$currentBuild.currentResult", 
        thumbnail: 'https://www.jenkins.io/images/logos/pixelart/jenkins-pixelart-32.png', 
        title: 'Team4-P2',
        // create env
        webhookURL: "${env.discord_webhook}"
}

return this