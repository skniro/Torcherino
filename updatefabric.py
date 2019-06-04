from urllib.request import urlopen
from xml.etree.ElementTree import fromstring
from argparse import ArgumentParser
from os.path import exists
from time import time

maven_url = "https://maven.fabricmc.net/net/fabricmc/{}/maven-metadata.xml"


def main(args: dict) -> str:
    if not exists(args['file']):
        print(f"Failed to update: The file '{args['file']}' does not exist.", end="")
        return 3
    output = []
    not_changed = True
    try:
        format_args = dict(fabric_version=get_release('fabric-api/fabric-api'), loader_version=get_release('fabric-loader'))
        format_args['minecraft_version'], format_args['yarn_version'] = get_release('yarn').rsplit('+', 1)
    except Exception as e:
        print(str(e), end="")
        return 3
    with open(args['file']) as f:
        for line in f:
            if " = " in line:
                key, value = line.split(" = ", 1)
                if key in format_args:
                    new_value = format_args[key]
                    output.append(f"{key} = {new_value}\n")
                    if new_value not in value: print(f"Updated {key} from {value[0:-1]} to {new_value}"); not_changed = False
                else: output.append(line)
            else: output.append(line)
    if not_changed:
        print("Already up to date. ", end="")
        return 1
    with open(args["file"], "w") as f: f.writelines(output)
    return 2


def get_release(package: str) -> str:
    try:
        version = fromstring(urlopen(maven_url.format(package)).read()).find("versioning/release")
        if hasattr(version, 'text'): return version.text
    except Exception: pass
    raise Exception(f"Failed to retrieve maven data for {package}.")

    
if __name__ == "__main__":
    start = time()
    parser = ArgumentParser()
    parser.add_argument("-f", "--file", default="gradle.properties", help="the gradle.properties file to update")
    exitcode = main(parser.parse_args().__dict__)
    end = time()
    print(f"Finished in {end-start} seconds.")
    exit(exitcode)
