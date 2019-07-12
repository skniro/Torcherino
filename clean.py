from os import system

system("gradlew --stop")
system("rmdir /q /s .gradle\loom-cache")
system("gradlew genSources")
input("Press any key to exit.")
