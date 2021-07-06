
node("master") {
    def stagesToRun = [:]
    stage("stage 1") {
        println "hello"
        sh "ls"
        sh "echo $BRANCH_NAME"
        checkout scm
        library identifier: '', retriever: modernSCM(scm: [$class: 'GitSCMSource', credentialsId: 'aypas-account', remote: 'https://github.com/2105-may24-devops/project2-team4', traits: [gitBranchDiscovery()]], libraryPath: 'vars/sayHello.groovy/')
        sayHello("dave")
    }
}