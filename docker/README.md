# Fiware Cepheus with Docker

## How to use this Dockerfile

You can build a docker image based on this [Dockerfile](Dockerfile).
This image will contain both cepheus-cep and cepheus-broker instances,
respectively exposed port `8080` and `8081`.

This Dockerfile uses the prebuild JARs from the Sonatype repository.

This requires that you have [docker](https://docs.docker.com/installation/) installed on your machine.

## Pull the image

You can pull the latest image from [Docker Hub](https://hub.docker.com/r/orangeopensource/fiware-cepheus/).

	docker pull orangeopensource/fiware-cepheus

## Build the image

    docker build -t orangeopensource/fiware-cepheus

The parameter `-t orangeopensource/fiware-cepheus` gives the image a name. This name could be anything, or even include an organization like `-t org/orangeopensource`.
This name is later used to run the container based on the image.

If you want to know more about images and the building process you can find it in [Docker's documentation](https://docs.docker.com/userguide/dockerimages/).

## Run the container

The following line will run the container exposing port `8080` (for cepheus-cep) and `8081 (for cepheus-broker), give it a name (in this case `cepheus1`).

    docker run -d --name cepheus1 -p 8080:8080 -p 8081:8081 orangeopensource/fiware-cepheus

The `-d` option detaches the docker run from the terminal.
As a result of this command, there is a `cepheus1` instance running in the background.

To see the logs:

    docker logs cepheus1

Try to see if it works now with:

    curl localhost:8080/v1/admin/config

To shutdown the container:

    docker stop cepheus1

### A few points to consider

* The name `cepheus1` can be anything and doesn't have to be related to the name given to the docker image in the previous section.
* In `-p 8080:8080` the first value represents the port to listen in on localhost, the second one the port in the container.



