# FIWARE Cepheus

[![Build Status](https://travis-ci.org/Orange-OpenSource/fiware-cepheus.svg?branch=master)](https://travis-ci.org/Orange-OpenSource/fiware-cepheus)
[![Coverity Scan Status](https://scan.coverity.com/projects/5913/badge.svg)](https://scan.coverity.com/projects/5913)
[![Coverage Status](https://coveralls.io/repos/Orange-OpenSource/fiware-cepheus/badge.svg?branch=master&service=github)](https://coveralls.io/github/Orange-OpenSource/fiware-cepheus?branch=master)
[![Docs Status](https://readthedocs.org/projects/fiware-cepheus/badge/?version=latest)](https://readthedocs.org/projects/fiware-cepheus/)
[![GNU GPL Version 2 Licence](http://img.shields.io/:license-gpl2-blue.svg)](LICENSE.txt)

This is the code repository for the Fiware-Cepheus project.

This project is part of [FIWARE](http://www.fiware.org) under the Gateway Data Handling GE subproject.
Check also the [FIWARE Catalogue entry for Cepheus](http://catalogue.fiware.org/enablers/gateway-data-handling-ge-espr4fastdata)

## Renaming

The previous name of the project was "EspR4FastData".
It was renamed to a more generic name (using the convention of other FIWARE Generic Enablers).

## Overall description

Fiware-Cepheus provides NGSI-compatible gateway level components.
This project contains two Spring Boot applications and a common library :

* cepheus-cep: A CEP (Complex Event Processor) engine.
* cepheus-broker: A light broker (NGSI forwarding-only).
* cepheus-ngsi : a client/server NGSI library.

## Quick instructions

You can find the complete build and installation instructions for each components in the administrator guide [here](doc/admin).

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

### Installing from Debian packaging

Using `mvn install` will create two debian packages: cepheus-cep-{version}.deb and cepeheus-lb-{version}.deb.
Then you can install these packages lauching :

    dpkg -i cepheus-cep-{version}.deb
    dpkg -i cepheus-broker-{version}.deb

## Running

The project provides two SpringBoot applications. You can run them simply with:

    java -jar cepheus-cep.jar
    java -jar cepheus-broker.jar

## User & programming guide

The user & programming guide can be found under the [/doc](doc) folder.
It is also available though readthedocs.org: [User guide](http://fiware-cepheus.readthedocs.org/en/latest/)

## Administrator manual

The administrator manual can be found under the [/doc/admin](doc/admin) folder.
It is also available though readthedocs.org: [Administrator manual](http://fiware-cepheus.readthedocs.org/en/latest/)

## Examples

Some simple examples to learn how to use Cepheus-CEP and Cepheus-Broker can be found under the [doc/examples](doc/examples) folder.

## License

FIWARE Cepheus is licensed under the [GNU General Public License Version 2](LICENSE.txt).

## Support

Ask your programming questions using [stackoverflow](http://stackoverflow.com/questions/tagged/fiware-cepheus) and your general questions on [FIWARE Q&A](https://ask.fiware.org/questions/).
In both cases please use the tag `fiware-cepheus`.
