#!/bin/bash
# Setup Cepheus-CEP and Cepheus-Broker on Ubuntu 14.04 for Fiware Lab

# Cepheus version
REPO="releases"
VERSION="LATEST"

# Check and echo all commands
set -e
set -x

# Install curl and add-apt-repository
sudo apt-get install -q -y -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold" curl
sudo apt-get install -q -y -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold" software-properties-common

# Add openjdk 8 PPA
sudo add-apt-repository -y ppa:openjdk-r/ppa
sudo apt-get update -q

# Install openjdk 8 JRE
sudo apt-get install -q -y -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold" openjdk-8-jre-headless

# Download Cepheus-CEP and Cepheus-Broker
curl -L -o cepheus-cep.deb "http://oss.sonatype.org/service/local/artifact/maven/redirect?r=$REPO&g=com.orange.cepheus&a=cepheus-cep&v=$VERSION&p=deb"
curl -L -o cepheus-broker.deb "http://oss.sonatype.org/service/local/artifact/maven/redirect?r=$REPO&g=com.orange.cepheus&a=cepheus-broker&v=$VERSION&p=deb"

# install debian package
sudo dpkg -i cepheus-broker.deb
sudo dpkg -i cepheus-cep.deb

# for Fiware Lab we need to set java variable java.security.egd to file:/dev/./urandom
sudo sed -i -e "s/java -jar/java -jar -Djava.security.egd=file:\/dev\/.\/urandom/g" /etc/init.d/cepheus-broker
sudo sed -i -e "s/java -jar/java -jar -Djava.security.egd=file:\/dev\/.\/urandom/g" /etc/init.d/cepheus-cep

sudo service cepheus-broker restart
sudo service cepheus-cep restart





