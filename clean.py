from os import system

if __name__ == "__main__":
    system("gradlew cleanLoomBinaries")
    system("gradlew cleanLoomMappings")
    system("gradlew genSources")
    input("Press any key to exit.")
