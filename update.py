from os import system as cmd
cmd("python updatefabric.py")
cmd("gradlew --stop")
cmd("gradlew cleanLoomBinaries cleanLoomMappings")
cmd("gradlew")
cmd("gradlew cleanIdea openIdea")
input("Press any key to exit.")
