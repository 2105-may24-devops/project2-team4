#!/usr/bin/env groovy


// create 2 scripts. 
// One to encapsulate the discord bot
// Another to make sure every neccesary is inside the agent 
// before running the pipeline...

// might as well add the two top functions into scripts as well.

def hello(String name = 'human') {
    sh "ls"
    return 10
}


return this