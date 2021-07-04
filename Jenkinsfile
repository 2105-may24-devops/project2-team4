@NonCPS
def findChangedServices() {
    def stagesToRun = ["flashcard-service": false,
                       "gateway-service": false,
                       "quiz-service": false]
    // def sonarProjectKeys = ["flashcard-service": "2105-may24-devops-p2t4-flashcard",
    //                         "gateway-service": "2105-may24-devops_p2t4-gateway",
    //                         "quiz-service": "2105-may24-devops-p2t4-quiz"]
    def list = currentBuild.changeSets
    for (int i=0; i < list.size(); i++) {
        // println "${list[i]}, ${list[i].getClass()}, ${list[i].getKind()}"
        def change_iterator = list[i].iterator()
        while (change_iterator.hasNext()) {
            def item = change_iterator.next()
            for (String paths : item.getAffectedPaths()) {
                //println 
                switch (paths.split("/")[0]) {
                    case "flashcard-service":
                        stagesToRun["flashcard-service"] = true
                        break
                    case "gateway-service":
                        stagesToRun["gateway-service"] = true
                        break
                    case "quiz-service":
                        stagesToRun["quiz-service"] = true
                        break
                }
            }
        }
    }
    return stagesToRun
}

@NonCPS
def findChangedDockerfiles() {
    
}

node("master") {
    def keys = ["flashcard-service", "gateway-service", "quiz-service"]
    def sonarProjectKeys = ["flashcard-service": "2105-may24-devops-p2t4-flashcard",
                            "gateway-service": "2105-may24-devops_p2t4-gateway",
                            "quiz-service": "2105-may24-devops-p2t4-quiz"]
    stage("Build, Test and Analyze") {
        println "hello"
        sh "ls"
        sh "echo $BRANCH_NAME"
        checkout scm
        sh "ls"
        // find which services were updated in the most recent push and only run sonarcloud analysis on those.
        // println "${currentBuild.changeSets}, ${currentBuild.changeSets.getClass()}"

        def info = findChangedServices()
        def parallelSteps = [:]
        for (key in info.keySet()) {
            println "this is the key $key"
            def project_key = sonarProjectKeys[key]
            def run = info[key]
            def directory = key
            parallelSteps[key] = {
                if (run) {
                    // compile, analyze and upload tests
                    withCredentials([string(credentialsId: 'sonar_auth_token', variable: 'sonar_auth_token')]) {
                        dir(directory) {
                            sh "mvn surefire-report:report package"
                            sh "mvn sonar:sonar -Dsonar.login=${sonar_auth_token} -Dsonar.host.url=https://sonarcloud.io -Dsonar.organization=2105-may24-devops -Dsonar.projectKey=${project_key} -Dsonar.java.binaries=target"
                            publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'target/site', reportFiles: 'surefire-report.html', reportName: "JUnit results for $directory", reportTitles: "$directory"])
                            jar_file = sh script: 'ls target | grep -E *.jar$', returnStdout: true
                            archiveArtifacts artifacts: "target/${jar_file}", followSymlinks: false
                        }
                    }
                } else {
                    println "No changes detected in $directory"
                }
            }
        }
        parallel(parallelSteps)
    }

    stage("Create and Push to Docker Hub") {

    }
}