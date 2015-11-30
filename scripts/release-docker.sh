#!/bin/sh
# Build and publish tagged Docker images
# Requires TAG env var

# Require the IP env variable
if [ -z "$TAG" ]; then
	echo "Please define the TAG env variable"
	exit 1
fi

set -e
set -x

docker build -t orangeopensource/fiware-cepheus:${TAG} docker
docker build -t orangeopensource/fiware-cepheus-broker:${TAG} cepheus-broker/docker
docker build -t orangeopensource/fiware-cepheus-cep:${TAG} cepheus-cep/docker

docker push orangeopensource/fiware-cepheus:${TAG}
docker push orangeopensource/fiware-cepheus-broker:${TAG}
docker push orangeopensource/fiware-cepheus-cep:${TAG}
