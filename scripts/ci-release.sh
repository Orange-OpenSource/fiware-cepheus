#!/bin/bash
# This script is triggered by Travis-CI when a new tag applied

set -e
set -x

# Build and Deploy on staging release
#mvn clean nexus-staging:release -Prelease -settings=settings.xml -DskipTests=true -Dgpg.passphrase=$GPG_PASSPHRASE -Dgpg.keyname=$GPG_KEYNAME

# Trigger Docker Automated build
#curl -s -H "Content-Type: application/json" --data '{"source_type": "Tag", "source_name": "0.1.1"}' -X POST "https://registry.hub.docker.com/u/$DOCKER_USER/fiware-cepheus/trigger/$DOCKER_KEY/"

# Publish DEB packages to bintray.com Debian repository
#curl -s -T "cepheus-cep/target/cepheus-cep_$TRAVIS_TAG_all.deb" -umarc4orange:$BINTRAY_KEY "https://api.bintray.com/content/orange-opensource/Fiware-Cepheus/Cepheus-CEP/$TRAVIS_TAG/cepheus-cep_$TRAVIS_TAG_all.deb;publish=1"
#curl -s -T "cepheus-broker/target/cepheus-broker_$TRAVIS_TAG_all.deb" -umarc4orange:$BINTRAY_KEY "https://api.bintray.com/content/orange-opensource/Fiware-Cepheus/Cepheus-Broker/$TRAVIS_TAG/cepheus-broker_$TRAVIS_TAG_all.deb;publish=1"
