#!/usr/bin/env groovy

// returns true if a build with artifacts exists;
// returns false if it does not;
def artifactsExist(branch) {
    try {
        copyArtifacts filter: "target/*.jar", projectName: "project2-team4/$branch", selector: lastWithArtifacts()
        return true
    } catch(Exception e) {
        return false
    }
}

return this