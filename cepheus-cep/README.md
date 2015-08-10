# Cepheus-CEP

Cepheus-CEP is a CEP (Complex Event Processor), it uses the [Esper](http://www.espertech.com/esper/) engine.
This engine provides a lot of features (aggregation, filters, rate limiting, correlation, partitionning...) with a nice [SQL like syntax](http://www.espertech.com/esper/release-5.2.0/esper-reference/html/epl_clauses.html).

Goal:

* process basic Context Entities from NGSI sensors
* generate higher levels of abstraction (Room, Floor, Building)
* at the gateway level (runs on Raspberry Pi)

## Build and Install

### Requirements

* JAVA 8
* Maven 2 (for build)

### Build and run via Maven

    git clone https://github.com/Orange-OpenSource/fiware-cepheus.git
    cd fiware-cepheus/cepheus-cep
    mvn spring-boot:run

### Installing from [Sonatype Central Maven](http://central.sonatype.org/) with Maven

    mvn dependency:get -DgroupId=com.orange.cepheus -DartifactId=cepheus-cep -Dversion=4.4.3-SNAPSHOT -Dtransitive=false

### Installing from [Sonatype Central Maven](http://central.sonatype.org/) using wget

    wget "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.orange.cepheus&a=cepheus-cep&v=LATEST"

## Running

Fiware-cepheus is a SpringBoot application. You can run it as a service.

    java -jar cepheus-cep.jar

### Configuration file

The configuration file location is stored in application.properties.
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

The complete user manual can be found [here](../doc/manual.md)

## License

Fiware-cepheus is licensed under GNU General Public License Version 2.
