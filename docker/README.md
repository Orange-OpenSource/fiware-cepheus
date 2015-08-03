# How to use this Dockerfile

You can build a docker image based on this Dockerfile. This image will contain only a fiware-cepheus instance, exposing port `8080`. This requires that you have [docker](https://docs.docker.com/installation/) installed on your machine.

## Pull the image
You can pull the image from [Docker Hub Registry](https://registry.hub.docker.com).

	docker pull orangeopensource/fiware-cepheus

## Build the image

This is an alternative approach than the one presented.
You only need to do this once in your system:

	docker build -t cepheus-cep

The parameter `-t cepheus-cep` gives the image a name. This name could be anything, or even include an organization like `-t org/orangeopensource`. This name is later used to run the container based on the image.

If you want to know more about images and the building process you can find it in [Docker's documentation](https://docs.docker.com/userguide/dockerimages/).

## Run the container

The following line will run the container exposing port `8080`, give it a name -in this case `cepheus-cep1` and present a bash prompt.

	  docker run -d --name cepheus-cep1 -p 8080:8080 cepheus-cep --debug

As a result of this command, there is a fiware-cepheus instance listening on port 8080 on localhost.

See the logs :

	  docker logs cepheus-cep1

Try to see if it works now with

	curl localhost:8080/v1/admin/config

A few points to consider:

* The name `cepheus-cep1` can be anything and doesn't have to be related to the name given to the docker image in the previous section.
* In `-p 8080:8080` the first value represents the port to listen in on localhost. If you wanted to run a second espr4fastdata on your machine you should change this value to something else, for example `-p 8081:8080`.
* Anything after the name of the container image (in this case `cepheus-cep`) is interpreted as a parameter for the instance. In this case we set level log to DEBUG.

