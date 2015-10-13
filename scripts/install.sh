#!/bin/bash
# Setup Cepheus-CEP and Cepheus-Broker on Ubuntu 14.04 (trusty)

# Cepheus version

# Check and echo all commands
set -e
set -x

# Update the machine was already done
#sudo apt-get update
#sudo apt-get upgrade -y

# Install curl and add-apt-repository
sudo apt-get install -q -y -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold" curl
sudo apt-get install -q -y -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold" software-properties-common

# Add openjdk 8 PPA
sudo add-apt-repository -y ppa:openjdk-r/ppa
sudo apt-get update -q

# Install openjdk 8 JRE
sudo apt-get install -q -y -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold" openjdk-8-jre-headless

# Download Cepheus-CEP and Cepheus-Broker
curl -L -o cepheus-cep.jar "http://oss.sonatype.org/service/local/artifact/maven/redirect?r=$REPO&g=com.orange.cepheus&a=cepheus-cep&v=$VERSION&p=jar"
curl -L -o cepheus-broker.jar "http://oss.sonatype.org/service/local/artifact/maven/redirect?r=$REPO&g=com.orange.cepheus&a=cepheus-broker&v=$VERSION&p=jar"

# launch Cepheus-CEP and Cepheus-Broker
nohup java -jar -Djava.security.egd=file:/dev/./urandom cepheus-cep.jar --logging.config=file --logging.file=cep.log --port=8080 2>> /dev/null >> /dev/null &
nohup java -jar -Djava.security.egd=file:/dev/./urandom cepheus-broker.jar --spring.datasource.url=jdbc:sqlite:cepheus-broker.db --logging.config=file --logging.file=broker.log --port=8081 2>> /dev/null >> /dev/null &



