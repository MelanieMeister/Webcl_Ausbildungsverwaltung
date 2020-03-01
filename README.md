# OpenDolphin CRUD Template

This project provides a template for using [OpenDolphin](http://open-dolphin.org) 

- with plain Java 
- with a simple Servlet started through Jetty
- with JavaFX as the client technology
- with Gradle for build automation.

 
 ## Projekt
Liste mit allen Aus- und Weiterbildungen von Mitarbeitenden eines Unternehmens.

## Quick Setup

Prerequisite: Java 8.

Git clone to a location of your liking and you will see a gradle multi-project build.

It contains the following modules/projects
- client: in a remote scenario, this is the client. It typically contains only view classes.
- server: in a remote scenario, this is the server. It typically contains controller actions.
- shared: this one is totally optional. If used, it typically contains shared constants between client and server.
- combined: combines all the above in one JVM for starting with the in-memory configuration for develop/test/debug.


## Running the samples

### JavaFX example

Using Gradle you can call the following to start the application in **combined mode**, i.e. both server and client in a single JVM.

    ./gradlew run (or 'gradlew run')


When running the application in **server mode** make sure that the server application is running too. To do so, call

    ./gradlew appRun (or 'gradlew appRun')
    
Make sure that you are using Java 8 for running the server. JAVA_HOME should be set accordingly. On Mac you can do this via

    export JAVA_HOME=$(/usr/libexec/java_home -v 1.8.0_172)
    
With a server running, you can start a client. Run from the client-module 

    myapp.ClientOnlyStarter
    
You can start serveral clients. They will be synchronized via the Dolphin Event Bus.
    
## More Info

This has only been a first glance into the way that OpenDolphin operates.

Many more features are available and you may want to check out the
- user guide (http://open-dolphin.org/download/guide/index.html), the
- other demo sources (http://github.com/canoo/open-dolphin/tree/master/subprojects/demo-javafx), or
- the video demos (http://www.youtube.com/user/dierkkoenig).
