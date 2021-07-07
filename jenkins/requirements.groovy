#!/usr/bin/env groovy

def checkRequirements(requires) {
    for (int i=0; i<requires.size(); i++) {
        def exit_code = sh returnStatus: true, script: "which ${requires.get(i)}"
        if (exit_code == 1) {
            return false
        }
    }
    return true
}

return this