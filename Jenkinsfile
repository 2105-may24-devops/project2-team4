node("master") {
    stage("stage 1") {
        println "hello"
        sh "ls"
        sh "echo $BRANCH_NAME"
        checkout scm
        sh "ls"
        def services = []
        println "${currentBuild.changeSets}"
        def change = currentBuild.changeSets
        for (int i = 0; i < change.size(); i++) {
            println "${change[i].items}"
            def paths = change[i].getPaths()
            for (String path : paths) {
                println "${path}"
            }
        }
    }
}