from os import system

if __name__ == "__main__":
    exitcode = system("python updatefabric.py")
    if exitcode == 2:
        system("gradlew --stop")
        system("gradlew cleanLoomBinaries")
        system("gradlew cleanLoomMappings")
        system("gradlew genSources")
        system("gradlew cleanIdea openIdea")
    elif exitcode == 1:
        system("gradlew openIdea")
    input("Press any key to exit.")
