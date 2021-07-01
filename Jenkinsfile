node("master") {
    stage("stage 1") {
        println "hello"
        sh "ls"
        sh "echo $BRANCH_NAME"
        checkout scm
        sh "ls"
        def services = []
        println "${currentBuild.changeSets}, ${currentBuild.changeSets.getClass()}"
        def list = currentBuild.changeSets
        for (int i=0; i < list.size(); i++) {
            println "${list[i]}, ${list[i].getClass()}"
            list[i].forEach(println "${item}, ${item.getClass()}")
        }
    }
}