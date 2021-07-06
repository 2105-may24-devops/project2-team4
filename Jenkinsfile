
node("master") {
    def stagesToRun = [:]
    stage("stage 1") {
        println "hello"
        sh "ls"
        sh "echo $BRANCH_NAME"
        checkout scm
        def func =  load("vars/sayHello.groovy")
        func.hello("dave")
        println func
    }
}