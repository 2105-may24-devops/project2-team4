@NonCPS
def getMapValue(jarMap, key) {
    return jarMap[key]
}

node() {
    // add to env files
    def requirements = ["mvn", "docker", "kubectl", "helm", "newman"]
    def sonarProjectKeys = ["flashcard-service": "2105-may24-devops-p2t4-flashcard",
                            "gateway-service": "2105-may24-devops_p2t4-gateway",
                            "quiz-service": "2105-may24-devops-p2t4-quiz"]
    // find a way to make this dynamic (you can extract the version number through groovy)
    def jarFiles = [
        "gateway-service": "gateway-service-0.0.1-SNAPSHOT.jar",
        "flashcard-service": "flashcard-service-0.0.1-SNAPSHOT.jar",
        "quiz-service": "quiz-service-0.0.1-SNAPSHOT.jar"
    ]

    def serviceChangeSet = null
    def dockerChangeSet = null
    def testStageResult = null
    stage("Build, Test and Analyze") {

        // create env variable which ends succesfully without running anything if branch is 'master'

        def checkout_details = checkout scm

        def meetsRequirements = load("jenkins/requirements.groovy").checkRequirements(requirements)
        if (!meetsRequirements) {
            currentBuild.result = 'ABORTED'
            error("Agent $NODE_NAME doesn't meet requirements")
        }
        def artifactsExist = load("jenkins/lastBuildWithArtifacts.groovy")
        def changes = load("jenkins/changes.groovy")
        // def discord = load("jenkins/discord.groovy")
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
                    // update current build with most recent build's artifact for the given service
                    copyArtifacts filter: "target/${serviceJarFile}", projectName: "project2-team4/$BRANCH_NAME", selector: lastWithArtifacts()
                    archiveArtifacts artifacts: "target/${serviceJarFile}", followSymlinks: false
                }
            }
        }
        parallel(parallelSteps)

        // docker section
        parallelDocker = [:]
        dockerChangeSet = changes.findChangedDockerfiles()

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
                    // if master
                    def containerName = "${env.container_registry}/${serviceName}:${BRANCH_NAME}"
                    if (serviceBoolean) {
                        // serviceBoolean being true means given service was just updated and compiled, and jar file
                        // jar file exists in the target directory of the given service
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

        // def desc = discord.createDescription(dockerChangeSet, serviceChangeSet)
        // discord.sendDiscordMessage(desc, "Deploying to AKS Development to Run Postman Tests")
    }

    stage("Deploy to AKS Test Environment") {
        sh "ls"
        sh "kubectl config use-context ${env.development_cluster}"
        sh "helm upgrade test helm/testchart -i"
        deployStatus = sh script: "kubectl wait --for=condition=ready pod --all --timeout=120s", returnStatus: true
        if (deployStatus != 0) {
            currentBuild.result = 'FAILURE'
            error("Jenkins failed to deploy project to development AKS cluster")
        }
        
        
        // dont commit
        def url = sh script: "kubectl get svc/gateway-svc -o jsonpath='{.status.loadBalancer.ingress[0].ip}'", returnStdout: true
        println url
        println "${url}:8080"
        // sh script: "helm install test helm/testchart --set ingress-nginx.extraArgs.watch-namespace=null"
        // sh "sleep 60s"
        // run newman tests
        def newmanResults = sh script: "newman run postman/kube_tests.json --timeout-request 1500 --global-var 'base_url=${url}:8080' -r html", 
                               returnStatus: true
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'newman/', reportFiles: '*.html', reportName: "Postman Tests for $BRANCH_NAME", reportTitles: "$BRANCH_NAME Postman Tests"])
        
        if (newmanResults == 0) {
            testStageResult = true
        } else {
            testStageResult = false
        }
    }

    stage("Deployment") {
        sh "kubectl config use-context ${env.production_cluster}"
        sh "helm upgrade test helm/testchart -i"
        deployExitStatus = sh script: "kubectl wait --for=condition=ready pod --all --timeout=120s", returnStatus: true
        def productionStatus = true
        if (deployExitStatus != 0) {
            productionStatus = false
        }
        def discord = load("jenkins/discord.groovy")
        println "${testStageResult} tests"
        def desc = discord.createDescription(dockerChangeSet, serviceChangeSet, testStageResult, productionStatus)
        discord.sendDiscordMessage(desc, "Leeeeeroy Jenkins!")
    }
}