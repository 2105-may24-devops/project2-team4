
node("master") {
    def stagesToRun = [:]
    stage("stage 1") {
        println "hello"
        sh "ls"
        sh "echo $BRANCH_NAME"
        checkout scm
        library "project2-team4@jenkins-library-test"
       
        sayHello("dave")
    }
}