@Library("project2-team4@jenkins-library-test")_

node("master") {
    def stagesToRun = [:]
    stage("stage 1") {
        println "hello"
        sh "ls"
        sh "echo $BRANCH_NAME"
        checkout scm
        def f = load 'dev/sayHello.groovy'
        f("hello")

    }
}