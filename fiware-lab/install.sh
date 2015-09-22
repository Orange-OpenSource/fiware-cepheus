#!/bin/bash
# Setup Cepheus-CEP and Cepheus-Broker on Ubuntu 14.04 (trusty)

# Cepheus version
REPO="snapshots" # "releases"
VERSION="LATEST" # "4.4.3"

# Check and echo all commands
set -e
set -x

# Update the machine
sudo apt-get update
sudo apt-get upgrade -y

# Install curl and add-apt-repository
sudo apt-get install -y curl
sudo apt-get install -y software-properties-common

# Add openjdk 8 PPA
sudo add-apt-repository -y ppa:openjdk-r/ppa
sudo apt-get update

# Install openjdk 8 JRE
sudo apt-get install -y openjdk-8-jre-headless

# Download Cepheus-CEP and Cepheus-Broker
curl -L -o cepheus-cep.deb "http://oss.sonatype.org/service/local/artifact/maven/redirect?r=$REPO&g=com.orange.cepheus&a=cepheus-cep&v=$VERSION&p=deb"
curl -L -o cepheus-broker.deb "http://oss.sonatype.org/service/local/artifact/maven/redirect?r=$REPO&g=com.orange.cepheus&a=cepheus-broker&v=$VERSION&p=deb"

# Install Cepheus-CEP and Cepheus-Broker
sudo dpkg -i cepheus-cep.deb
sudo dpkg -i cepheus-broker.deb


