# FIWARE Cepheus

[![Build Status](https://travis-ci.org/Orange-OpenSource/fiware-cepheus.svg?branch=master)](https://travis-ci.org/Orange-OpenSource/fiware-cepheus)
[![Coverity Scan Status](https://scan.coverity.com/projects/5913/badge.svg)](https://scan.coverity.com/projects/5913)
[![Coverage Status](https://coveralls.io/repos/Orange-OpenSource/fiware-cepheus/badge.svg?branch=master&service=github)](https://coveralls.io/github/Orange-OpenSource/fiware-cepheus?branch=master)

This is the code repository for fiware-cepheus, the reference implementation of the Gateway Data Handling GE.

This project is part of [FIWARE](http://www.fiware.org).
Check also the [FIWARE Catalogue entry for Cepheus](http://catalogue.fiware.org/enablers/gateway-data-handling-ge-espr4fastdata)

## Overall description

Fiware-cepheus is a SpringBoot Application. It uses the Esper CEP (Complex Event Processing) engine.
This Engine provides a lot of features (aggregation, filters, rate limiting, correlation, partitionning...) with a nice [SQL like syntax](http://www.espertech.com/esper/release-5.2.0/esper-reference/html/epl_clauses.html).

Goal:

* process basic Context Entities from NGSI sensors
* generate higher levels of abstraction (Room, Floor, Building)
* at the gateway level (runs on Raspberry Pi)


## Build and Install


### Requirements

* JAVA 8

### Build and install via Maven command

	mvn clean install

### Installing from pulling [Sonatype Central Maven](http://central.sonatype.org/)

	mvn -DgroupId=com.orange.cepheus -DartifactId=cepheus-cep -Dversion=4.4.3-SNAPSHOT -Dtransitive=false dependency:get

### Installing from Docker
The docker manual can be found [here](docker/README.md)

## Running

Fiware-cepheus is a SpringBoot application. You can run it as a service.

	java -jar cepheus-cep.jar

### Configuration file

The configuration file directory is stored in application.properties.
It's a simple json file which typical content is:

    {
	  "in": [
	    {
	      "id": "S.*",
	      "type": "TempSensor",
	      "isPattern": true,
	      "attributes": [
	        {
	          "name": "temp",
	          "type": "float"
	        }
	      ],
	      "providers": [
	        "http://localhost:1902/ngsi10"
	      ]
	    }
	  ],
	  "out": [
	    {
	      "id": "OUT1",
	      "isPattern": false,
	      "type": "TempSensorAvg",
	      "attributes": [
	        {
	          "name": "avgTemp",
	          "type": "float"
	        }
	      ],
	      "brokers": [
	        {
	          "url": "http://102.232.332:1903/v1",
	          "serviceName": "my",
	          "servicePath": "/test/path"
	        }
	      ]
	    }
	  ],
	  "statements": [
	    "INSERT INTO 'TempSensorAvg' SELECT 'OUT1' as id, avg(TempSensor.temp) as avgTemp FROM TempSensor.win:time(86400) WHERE TempSensor.id = 'S1' "
	  ]
	}

## User guide

The complete user manual can be found [here](doc/manual.md)

## License

Fiware-cepheus is licensed under GNU General Public License Version 2.
