@NonCPS
def findChangedServices() {
    def stagesToRun = ["flashcard-service": false,
                       "gateway-service": false,
                       "quiz-service": false]

    def list = currentBuild.changeSets
    for (int i=0; i < list.size(); i++) {
        // println "${list[i]}, ${list[i].getClass()}, ${list[i].getKind()}"
        def change_iterator = list[i].iterator()
        while (change_iterator.hasNext()) {
            def item = change_iterator.next()
            for (String paths : item.getAffectedPaths()) {
                //println 
                switch (paths.split("/")[0]) {
                    case "flashcard-service":
                        stagesToRun["flashcard-service"] = true
                        break
                    case "gateway-service":
                        stagesToRun["gateway-service"] = true
                        break
                    case "quiz-service":
                        stagesToRun["quiz-service"] = true
                        break
                }
            }
        }
    }
    return stagesToRun
}

@NonCPS
def findChangedDockerfiles() {
    def dockerChanges = ["flashcard-service": ['flashcard.Dockerfile', false],
                       "gateway-service": ['gateway.Dockerfile', false],
                       "quiz-service": ['quiz.Dockerfile', false]]
    def list = currentBuild.changeSets
    for (int i=0; i < list.size(); i++) {
        // println "${list[i]}, ${list[i].getClass()}, ${list[i].getKind()}"
        def change_iterator = list[i].iterator()
        while (change_iterator.hasNext()) {
            def item = change_iterator.next()
            for (String file : item.getAffectedPaths()) {
                changedFiles = file.split("/")
                if (changedFiles[0] == "dockerize") {
                    println "${changedFiles[changedFiles.length-1]} this is the thing ${changedFiles[changedFiles.length-1] == 'flashcard.Dockerfile'}"
                    switch (changedFiles[changedFiles.length-1]) {
                        case 'flashcard.Dockerfile':
                            println "this is it"
                            dockerChanges["flashcard-service"] = ['flashcard.Dockerfile', true]
                            break
                        case 'gateway.Dockerfile':
                            println "gate"
                            dockerChanges["gateway-service"] = ['gateway.Dockerfile', true]
                            break
                        case 'quiz.Dockerfile':
                            println "quiz"
                            dockerChanges["quiz-service"] = ['quiz.Dockerfile', true]
                            break
                    }
                }
            }
        }
    }
    return dockerChanges
}