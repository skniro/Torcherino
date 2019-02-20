import urllib.request as request
import xml.etree.ElementTree as ElementTree
import sys
import string

def main(args):
    mavenRepo = "https://maven.fabricmc.net/net/fabricmc/{}/maven-metadata.xml"
    templateargs = {}
    index = ""
    output = []
    file = "gradle.properties"
    for value in args:
        if value[0] == "-":
            index = value[1::]
        else:
            templateargs[index] = value
            index = ""   
    if "file" in templateargs:
        file = templateargs.pop("file")
    templateargs["fabric_version"] = getReleaseOrLatest(mavenRepo, "fabric")
    templateargs["loader_version"] = getReleaseOrLatest(mavenRepo, "fabric-loader")
    templateargs["minecraft_version"], templateargs["yarn_version"] = getReleaseOrLatest(mavenRepo, "yarn").split(".")
    print("Reading from file({0}).".format(file))
    with open(file, "r+") as f:
        while True:
            line = f.readline().split(" ")
            if line[0] == "": break
            if line[0] in templateargs:
                output += ["{} = {}{}".format(line[0], templateargs[line[0]], ("\n" if "\n" in line[2] else ""))]
            else:
                output += [" ".join(line)]
    print("Saving to file({0}).".format(file))
    with open(file, "w+") as f:
        f.writelines(output)


def getReleaseOrLatest(mavenURL: str, package: str) -> str:
    print("Downloading {} data".format(package))
    URL = mavenURL.format(package)
    mavenData = request.urlopen(URL).read()
    elementTree = ElementTree.fromstring(mavenData)
    version = elementTree.find("versioning/release")
    if version is not None:
        print("    Found version {}".format(version.text))
        return version.text
    else:
        version = elementTree.find("versioning/versions")
        if version is not None:
            print("    Found version {}".format(version[-1].text))
            return version[-1].text
        else:
            raise ValueError("Maven doesnt have version data???")


if __name__ == "__main__":
    main(sys.argv[1::])
