
node("master") {
    def stagesToRun = [:]
    stage("stage 1") {
        println "hello"
        sh "ls"
        sh "echo $BRANCH_NAME"
        checkout scm
        library identifier: '1', retriever: legacySCM(libraryPath: 'vars', scm: [$class: 'GitSCM', branches: [[name: '*/jenkins-library-test']], extensions: [], userRemoteConfigs: [[credentialsId: 'aypas-account', url: 'https://github.com/2105-may24-devops/project2-team4']]])
       
        sayHello("dave")
    }
}