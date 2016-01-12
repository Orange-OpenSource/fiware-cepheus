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
curl -L -o cepheus-cep.jar "http://oss.sonatype.org/service/local/artifact/maven/redirect?r=$REPO&g=com.orange.cepheus&a=cepheus-cep&v=$VERSION&p=jar"
curl -L -o cepheus-broker.jar "http://oss.sonatype.org/service/local/artifact/maven/redirect?r=$REPO&g=com.orange.cepheus&a=cepheus-broker&v=$VERSION&p=jar"

# create launcher script
echo '#!/bin/bash' > launcher-cepheus.sh
echo 'nohup java -jar -Djava.security.egd=file:/dev/./urandom cepheus-cep.jar --logging.config=file --logging.file=cep.log --port=8080 2>> /dev/null &' >> launcher-cepheus.sh
echo 'nohup java -jar -Djava.security.egd=file:/dev/./urandom cepheus-broker.jar --spring.datasource.url=jdbc:sqlite:cepheus-broker.db --logging.config=file --logging.file=broker.log --port=8081 2>> /dev/null >> /dev/null &' >> launcher-cepheus.sh
echo 'exit 0' >> launcher-cepheus.sh

sudo chmod 777 launcher-cepheus.sh

# launch Cepheus-CEP and Cepheus-Broker on boot
sudo sed -i -e '$i \/home/ubuntu/launcher-cepheus.sh\n' /etc/rc.local





