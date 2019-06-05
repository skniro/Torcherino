from os import system

if __name__ == "__main__":
    system("gradlew --stop")
    system("gradlew cleanLoomBinaries")
    system("gradlew cleanLoomMappings")
    system("gradlew genSources")
    system("gradlew cleanIdea openIdea")
    input("Press any key to exit.")
