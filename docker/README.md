# How to use this Dockerfile

You can build a docker image based on this Dockerfile. This image will contain only an espR4FastData instance, exposing port `8080`. This requires that you have [docker](https://docs.docker.com/installation/) installed on your machine.

## Pull the image
You can pull the image from [Docker Hub Registry](https://registry.hub.docker.com).

	docker pull orangeopensource/espr4fastdata

## Build the image

This is an alternative approach than the one presented.
You only need to do this once in your system:

	docker build -t espr4fastdata

The parameter `-t espr4fastdata` gives the image a name. This name could be anything, or even include an organization like `-t org/orangeopensource`. This name is later used to run the container based on the image.

If you want to know more about images and the building process you can find it in [Docker's documentation](https://docs.docker.com/userguide/dockerimages/).

## Run the container

The following line will run the container exposing port `8080`, give it a name -in this case `espr4fastdata1` and present a bash prompt.

	  docker run -d --name espr4fastdata1 -p 8080:8080 espr4fastdata --debug

As a result of this command, there is a espr4fastdata listening on port 8080 on localhost.

See the logs :

	  docker logs espr4fastdata1

Try to see if it works now with

	curl localhost:8080/v1/admin/config

A few points to consider:

* The name `espr4fastdata1` can be anything and doesn't have to be related to the name given to the docker image in the previous section.
* In `-p 8080:8080` the first value represents the port to listen in on localhost. If you wanted to run a second espr4fastdata on your machine you should change this value to something else, for example `-p 8081:8080`.
* Anything after the name of the container image (in this case `espr4fastdata`) is interpreted as a parameter for the espR4FastData. In this case we set level log to DEBUG.



