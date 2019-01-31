import urllib.request as request
import xml.etree.ElementTree as ET
import sys
import string

class CustomTemplate(string.Template):
    delimiter = "@"
    
def main(args):
    mavenRepo = "https://maven.fabricmc.net/net/fabricmc/{}/maven-metadata.xml"
    templateargs = {"template": ""}
    index = ""
    for value in args:
        if value[0] == "-":
            index = value[1::]
        else:
            templateargs[index] = value
            index = ""
    template = templateargs.pop("template")
    if not template == "":
        print("Downloading loom data")
        templateargs["loom_version"] = getLatestVersion(mavenRepo.format("fabric-loom"))
        print("Downloading fabric data")
        templateargs["fabric_version"] = getReleaseVersion(mavenRepo.format("fabric"))
        print("Downloading fabric-loader data")
        templateargs["loader_version"] = getReleaseVersion(mavenRepo.format("fabric-loader"))
        print("Downloading yarn data")
        mcyarn = getReleaseVersion(mavenRepo.format("yarn")).split(".")
        templateargs["minecraft_version"] = mcyarn[0]
        templateargs["yarn_version"] = mcyarn[1]
        print("Reading template file.")
        with open(template, "r+") as f:
            templateContents = CustomTemplate(f.read())
        print("Saving new file.")
        with open(template.replace("template_", ""), "w+") as f:
            f.write(templateContents.safe_substitute(templateargs))
                  
def getReleaseVersion(mavenURL: str) -> str:
    mavenData = request.urlopen(mavenURL).read()
    elementTree = ET.fromstring(mavenData)
    version = elementTree.find("versioning/release")
    if version is not None:
        return version.text
    else:
        raise ValueError("Maven doesnt have version data???")

def getLatestVersion(mavenURL: str) -> str:
    mavenData = request.urlopen(mavenURL).read()
    elementTree = ET.fromstring(mavenData)
    version = elementTree.find("versioning/versions")
    if version is not None:
        return version[-1].text
    else:
        raise ValueError("Maven doesnt have version data???")
    return "1"

if __name__ == "__main__":
    main(sys.argv[1::])
    
            
