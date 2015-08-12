# FIWARE Cepheus

[![Build Status](https://travis-ci.org/Orange-OpenSource/fiware-cepheus.svg?branch=master)](https://travis-ci.org/Orange-OpenSource/fiware-cepheus)
[![Coverity Scan Status](https://scan.coverity.com/projects/5913/badge.svg)](https://scan.coverity.com/projects/5913)
[![Coverage Status](https://coveralls.io/repos/Orange-OpenSource/fiware-cepheus/badge.svg?branch=master&service=github)](https://coveralls.io/github/Orange-OpenSource/fiware-cepheus?branch=master)

This is the code repository for fiware-cepheus, the reference implementation of the Gateway Data Handling GE.

This project is part of [FIWARE](http://www.fiware.org).
Check also the [FIWARE Catalogue entry for Cepheus](http://catalogue.fiware.org/enablers/gateway-data-handling-ge-espr4fastdata)

## Renaming

The previous name of the project was "EspR4FastData".
It was renamed to a more generic name (using the convention of other FIWARE enablers).

## Overall description

FIWARE Cepheus provides NGSI-compatible gateway level components.
This project contains two Spring Boot applications and a common library :

* cepheus-cep: A CEP (Complex Event Processor) engine.
* cepheus-lb: A light broker (NGSI forwarding-only).
* cepheus-ngsi : a client/server NGSI library.

## Build and Install

More information about building can be found in [cepheus-cep/README](cepheus-cep/README.md) and [cepheus-lb/README](cepheus-lightbroker/README.md).

### Requirements

* JAVA 8
* Maven 2 (for build)

### Build via Maven

    git clone https://github.com/Orange-OpenSource/fiware-cepheus.git
    cd fiware-cepheus
    mvn clean install

### Installing from Docker

Using Docker is the fastest way to have a working setup :

    docker pull orangeopensource/fiware-cepheus

Our docker manual can be found [here](docker/README.md)

## Running

Fiware-cepheus and fiware-lb are SpringBoot applications. You can run them simply with:

    java -jar cepheus-cep.jar
    java -jar cepheus-lb.jar

## User guide

The complete user manual can be found [here](doc/manual.md)

## License

Fiware-cepheus is licensed under [GNU General Public License Version 2](LICENCE.txt).
