from os import system

system("rmdir build /s /q")
system("gradlew --stop")
system("gradlew build")
input()