from os import system

system("gradlew --stop")
system("gradlew cleanLoomBinaries")
system("gradlew cleanLoomMappings")
system("gradlew genSources")
input("Press any key to exit.")
