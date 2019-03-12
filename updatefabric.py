from urllib.request import urlopen
from xml.etree.ElementTree import fromstring
from argparse import ArgumentParser
from os.path import exists
from time import time

maven_url = "https://maven.fabricmc.net/net/fabricmc/{}/maven-metadata.xml"


def main(args: dict) -> str:
	if not exists(args['file']): return f"Failed to update: The file '{args['file']}' does not exist."
	output = []
	format_args = dict(fabric_version=get_release('fabric'), loader_version=get_release('fabric-loader'))
	format_args['minecraft_version'], format_args['yarn_version'] = get_release('yarn').rsplit('.')
	with open(args['file']) as f:
		for line in f:
			first = line.split(" ", 1)[0]
			if first in format_args: output.append(f"{first} = {format_args[first]}\n")
			else: output.append(line)
	with open(args["file"], "w") as f: f.writelines(output)
	return "Updated successfully."


def get_release(package: str) -> str:
	version = fromstring(urlopen(maven_url.format(package)).read()).find("versioning/release")
	if hasattr(version, 'text'): return version.text
	raise ValueError(f"Maven does not have version data for {package}???")


if __name__ == "__main__":
	start = time()
	parser = ArgumentParser()
	parser.add_argument("-f", "--file", default="gradle.properties", help="the gradle.properties file to update")
	reason = main(parser.parse_args().__dict__)
	end = time()
	print(f"{reason} Finished in {end-start} seconds.")
