import os
os.system("python updatefabric.py -template template_build.gradle")
os.system("gradlew cleanLoomBinaries cleanLoomMappings")
os.system("gradlew")
os.system("gradlew cleanIdea openIdea")
input("Press any key to exit.")
