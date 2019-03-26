from os import system

if __name__ == "__main__":
    system("python updatefabric.py")
    system("gradlew --stop")
    system("gradlew cleanLoomBinaries cleanLoomMappings")
    system("gradlew genSources")
    system("gradlew cleanIdea openIdea")
    input("Press any key to exit.")
