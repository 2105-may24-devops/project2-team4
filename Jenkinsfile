// @NonCPS
// def findChangedServices() {
//     def stagesToRun = ["flashcard-service": false,
//                        "gateway-service": false,
//                        "quiz-service": false]

//     def list = currentBuild.changeSets
//     for (int i=0; i < list.size(); i++) {
//         // println "${list[i]}, ${list[i].getClass()}, ${list[i].getKind()}"
//         def change_iterator = list[i].iterator()
//         while (change_iterator.hasNext()) {
//             def item = change_iterator.next()
//             for (String paths : item.getAffectedPaths()) {
//                 //println 
//                 switch (paths.split("/")[0]) {
//                     case "flashcard-service":
//                         stagesToRun["flashcard-service"] = true
//                         break
//                     case "gateway-service":
//                         stagesToRun["gateway-service"] = true
//                         break
//                     case "quiz-service":
//                         stagesToRun["quiz-service"] = true
//                         break
//                 }
//             }
//         }
//     }
//     return stagesToRun
// }

// @NonCPS
// def findChangedDockerfiles() {
//     def dockerChanges = ["flashcard-service": ['flashcard.Dockerfile', false],
//                        "gateway-service": ['gateway.Dockerfile', false],
//                        "quiz-service": ['quiz.Dockerfile', false]]
//     def list = currentBuild.changeSets
//     for (int i=0; i < list.size(); i++) {
//         // println "${list[i]}, ${list[i].getClass()}, ${list[i].getKind()}"
//         def change_iterator = list[i].iterator()
//         while (change_iterator.hasNext()) {
//             def item = change_iterator.next()
//             for (String file : item.getAffectedPaths()) {
//                 changedFiles = file.split("/")
//                 if (changedFiles[0] == "dockerize") {
//                     println "${changedFiles[changedFiles.length-1]} this is the thing ${changedFiles[changedFiles.length-1] == 'flashcard.Dockerfile'}"
//                     switch (changedFiles[changedFiles.length-1]) {
//                         case 'flashcard.Dockerfile':
//                             println "this is it"
//                             dockerChanges["flashcard-service"] = ['flashcard.Dockerfile', true]
//                             break
//                         case 'gateway.Dockerfile':
//                             println "gate"
//                             dockerChanges["gateway-service"] = ['gateway.Dockerfile', true]
//                             break
//                         case 'quiz.Dockerfile':
//                             println "quiz"
//                             dockerChanges["quiz-service"] = ['quiz.Dockerfile', true]
//                             break
//                     }
//                 }
//             }
//         }
//     }
//     return dockerChanges
// }

@NonCPS
def getMapValue(jarMap, key) {
    return jarMap[key]
}



node("master") {
    def keys = ["flashcard-service", "gateway-service", "quiz-service"]
    def sonarProjectKeys = ["flashcard-service": "2105-may24-devops-p2t4-flashcard",
                            "gateway-service": "2105-may24-devops_p2t4-gateway",
                            "quiz-service": "2105-may24-devops-p2t4-quiz"]
    def jarFiles = [
        "gateway-service": "gateway-service-0.0.1-SNAPSHOT.jar",
        "flashcard-service": "flashcard-service-0.0.1-SNAPSHOT.jar",
        "quiz-service": "quiz-service-0.0.1-SNAPSHOT.jar"
    ]

    def serviceChangeSet

    stage("Build, Test and Analyze") {
        sh "echo $BRANCH_NAME"
        println "${env.flashcard_build} build id"
        // env.flashcard_build = "161"
        def checkout_details = checkout scm
        def artifactsExist = load("vars/lastBuildWithArtifacts.groovy") 
        def changes = load("vars/changes.groovy")
        // find which services were updated in the most recent push and only run sonarcloud analysis on those.
        // println "${currentBuild.changeSets}, ${currentBuild.changeSets.getClass()}"

        serviceChangeSet = changes.findChangedServices()
        println serviceChangeSet

        // compile, test and analyze all three services so that build artifacts for all services can be created
        // if they do not all exist
        if (currentBuild.previousSuccessfulBuild == null || !artifactsExist.artifactsExist("$BRANCH_NAME")) {
            for (key in serviceChangeSet.keySet()) {
                serviceChangeSet[key] = true
            }
        }
        // test, compile and build
        def parallelSteps = [:]
        for (key in serviceChangeSet.keySet()) {
            println "this is the key $key"
            def serviceJarFile = getMapValue(jarFiles, key)
            def project_key = sonarProjectKeys[key]
            def run = serviceChangeSet[key]
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
                    // update current build with most recent build's artifact's
                    copyArtifacts filter: "target/${serviceJarFile}", projectName: "project2-team4/$BRANCH_NAME", selector: lastWithArtifacts()
                    archiveArtifacts artifacts: "target/${serviceJarFile}", followSymlinks: false
                }
            }
        }
        parallel(parallelSteps)

        // docker section
        parallelDocker = [:]
        def dockerChangeSet = changes.findChangedDockerfiles()

        // needs: dockerfile of given service, boolean of dockerfile & service's jar, and jar file name

        for (key in dockerChangeSet.keySet()) {
            def serviceName = key
            def dockerFile = dockerChangeSet[key][0]
            def dockerFileBoolean = dockerChangeSet[key][1]
            def serviceBoolean = serviceChangeSet[key]
            def serviceJarFile = getMapValue(jarFiles, key)
            parallelDocker[key] = {
                if (dockerFileBoolean || serviceBoolean) {
                    withCredentials([usernamePassword(credentialsId: 'azure_container_registry_login', passwordVariable: 'password', usernameVariable: 'username')]) {
                        sh "docker login -u ${username} -p ${password} ${env.container_registry}"
                    }
                    def containerName = "${env.container_registry}/team4containers:${serviceName}-${checkout_details['GIT_COMMIT']}-${BRANCH_NAME}"
                    if (serviceBoolean) {
                        // serviceBoolean being true means given service was just updated and compiled, and jar file
                        // exists in service's target directory
                        sh "docker build -f dockerize/${dockerFile} -t ${containerName} ${serviceName}/target"
                    } else {
                        copyArtifacts filter: "target/${serviceJarFile}", projectName: "project2-team4/$BRANCH_NAME", selector: lastWithArtifacts()
                        sh "docker build -f dockerize/${dockerFile} -t ${containerName} target"
                    }
                    sh "docker push ${containerName}"
                    sh "docker image rm -f ${containerName}"
                    sh "docker logout"
                } else {
                    println "No changes detected in $dockerFile"
                }
            }
        }
        parallel(parallelDocker)
    }
}