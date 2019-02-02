from os import system as cmd
cmd("python updatefabric.py -in template_build.gradle")
cmd("gradlew cleanLoomBinaries cleanLoomMappings")
cmd("gradlew")
cmd("gradlew cleanIdea openIdea")
input("Press any key to exit.")
