# FIWARE Cepheus

[![Build Status](https://travis-ci.org/Orange-OpenSource/fiware-cepheus.svg?branch=master)](https://travis-ci.org/Orange-OpenSource/fiware-cepheus)
[![Coverity Scan Status](https://scan.coverity.com/projects/5913/badge.svg)](https://scan.coverity.com/projects/5913)
[![Coverage Status](https://coveralls.io/repos/Orange-OpenSource/fiware-cepheus/badge.svg?branch=master&service=github)](https://coveralls.io/github/Orange-OpenSource/fiware-cepheus?branch=master)
[![Docs Status](https://readthedocs.org/projects/fiware-cepheus/badge/?version=latest)](https://readthedocs.org/projects/fiware-cepheus/)
[![Docker Pulls](https://img.shields.io/badge/docker%20pulls-262%20MB-blue.svg)](https://hub.docker.com/r/orangeopensource/fiware-cepheus/)
[![Support badge]( https://img.shields.io/badge/support-sof-yellowgreen.svg)](http://stackoverflow.com/questions/tagged/fiware)
[![GNU GPL Version 2 Licence](http://img.shields.io/:license-gpl2-blue.svg)](LICENSE.txt)
[![Join the chat at https://gitter.im/Orange-OpenSource/fiware-cepheus](https://img.shields.io/badge/gitter-join%20chat%20→-brightgreen.svg)](https://gitter.im/Orange-OpenSource/fiware-cepheus?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This is the code repository for the Fiware-Cepheus project.

This project is part of [FIWARE](http://www.fiware.org) under the IoT Data Edge Consolidation GE project.
Check also the [FIWARE Catalogue entry for Cepheus](http://catalogue.fiware.org/enablers/iot-data-edge-consolidation-ge-cepheus)

### Renaming

The previous name of the project was "EspR4FastData".
It was renamed to a more generic name (using the convention of other FIWARE Generic Enablers).

## Overall description

Fiware-Cepheus provides NGSI-compatible gateway level components.
This project contains two Spring Boot applications:

* cepheus-cep: A CEP (Complex Event Processor) engine.
* cepheus-broker: A light broker (NGSI forwarding-only).

NGSI v1 implementation is provided by the [Orange-OpenSource/fiware-ngsi-api](https://github.com/Orange-OpenSource/fiware-ngsi-api) library.

## Quick instructions

You can find the complete build and installation instructions for each components in the administrator guide [here](doc/admin).

### Requirements

* JAVA 8
* Maven 2 (for build)
* OS/CPU supported by [Sqlite-JDBC](https://github.com/xerial/sqlite-jdbc) (for cepheus-broker)

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

### Running

The project provides two SpringBoot applications. You can run them simply with:

    java -jar cepheus-cep.jar
    java -jar cepheus-broker.jar

## Documentation

The user & programming guide can be found under the [/doc](doc) folder.
It is also available though readthedocs.org: [User guide](http://fiware-cepheus.readthedocs.org/en/latest/)

The administrator manual can be found under the [/doc/admin](doc/admin) folder.
It is also available though readthedocs.org: [Administrator manual](http://fiware-cepheus.readthedocs.org/en/latest/)

## Examples

Some simple examples to learn how to use Cepheus-CEP and Cepheus-Broker can be found under the [doc/examples](doc/examples) folder.

### LinkSprite-NGSI Sample

An example to use a Raspberry Pi with LinkSprite sensors and actuators communicating with Cepheus can be found [here](https://github.com/Orange-OpenSource/fiware-cepheus/tree/linksprite-ngsi)

## License

FIWARE Cepheus is licensed under the [GNU General Public License Version 2](LICENSE.txt).

## Support

Ask your programming questions using [Stackoverflow](http://stackoverflow.com/questions/tagged/fiware) and your general questions on [FIWARE Q&A](https://ask.fiware.org/questions/).
In both cases please use the tags `fiware` and `fiware-cepheus`.
