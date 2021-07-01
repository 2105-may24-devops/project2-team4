node("master") {
    stage("stage 1") {
        println "hello"
        sh "ls"
        sh "echo $BRANCH_NAME"
        checkout scm
        sh "ls"
        def services = []
        println "${currentBuild.changeSets.getAffectedPaths()}"
        def change = currentBuild.changeSets.getAffectedPaths()
        for (String path : change) {
            println "${path}"
        }
    }
}