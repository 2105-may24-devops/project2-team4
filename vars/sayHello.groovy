#!/usr/bin/env groovy

def hello(String name = 'human') {
    sh "ls"
    return 10
}


return this