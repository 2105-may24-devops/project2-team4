node("master") {
    def stagesToRun = [:]
    stage("stage 1") {
        println "hello"
        sh "ls"
        sh "echo $BRANCH_NAME"
        checkout scm
        sh "ls"
        def changedServices = ["flashcard-service", "gateway-service", "quiz-service", "dockerize"]
        println "${currentBuild.changeSets}, ${currentBuild.changeSets.getClass()}"
        def list = currentBuild.changeSets
        for (int i=0; i < list.size(); i++) {
            println "${list[i]}, ${list[i].getClass()}, ${list[i].getKind()}"
            def change_iterator = list[i].iterator()
            while (change_iterator.hasNext()) {
                def item = change_iterator.next()
                println "${item}, ${item.getClass()}"
                for (String paths : item.getAffectedPaths()) {
                    println paths
                }
            }
        }
    }
}