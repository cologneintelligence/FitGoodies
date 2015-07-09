#!/bin/bash

set -e

cd "$(dirname "$(readlink -f "$0")")"
[ -d tmp ] && rm -r tmp

if [ "$1" = "-b" ]; then
	mvn clean install
fi

mkdir tmp
cd tmp

mvn archetype:generate -B -DarchetypeCatalog=local \
	-DarchetypeVersion=1.3.0-SNAPSHOT \
	-DarchetypeArtifactId=archetype-fitgoodies-quickstart \
	-DarchetypeGroupId=de.cologneintelligence \
	-DgroupId=org.example -DartifactId=tmp -Dversion=1.0 \
	-Dgoals=verify

cd tmp
java -cp "target/classes/;target/test-classes/;../../fitgoodies/target/fitgoodies-1.3.0-SNAPSHOT.jar;D:\\maven\\repository\\com\\c2\\fit\\fit\\1.1\\fit-1.1.jar" de.cologneintelligence.fitgoodies.runners.FitRunner -d target/fit2 -s src/test/fixtures
