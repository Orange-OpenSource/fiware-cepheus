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

### Installing from [Sonatype Central Maven](http://central.sonatype.org/) using wget and dpkg

    wget "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.orange.cepheus&a=cepheus-cep&v=LATEST&p=deb"
    dpkg -i cepheus-cep.deb

## Running

Fiware-cepheus is a SpringBoot application. You can run it as a service.

    java -jar cepheus-cep.jar

## Application properties

This is the list of the application properties:

<table>
    <tr><th>Name</th><th>Description</th><th>Default value</th></tr>
    <tr><td>server.port</td><td>port used</td><td>8080</td></tr>
    <tr><td>config.file</td><td>configuration file location</td><td>/tmp/cepheus.json</td></tr>
    <tr><td>subscriptionManager.periodicity</td><td>Periodicity of the subscription manager task</td><td>300000</td></tr>
    <tr><td>subscriptionManager.duration</td><td>Duration of a NGSI subscription</td><td>PT1H</td></tr>
    <tr><td>logging.level.com.orange.cepheus.cep</td><td>log level</td><td>INFO</td></tr>
</table>

Default properties are defined in [application.properties](src/main/resources/application.properties).

### Command line parameters

You can modify all the application properties from the command line:

    java -jar cepheus-cep.jar --property=value

Example:

    java -jar cepheus-cep.jar --server.port=9091 --config.file=/var/cepheus/cep-config.json

### Custom applications.properties file

If you want to customize application properties after application has been packaged,
you can override the default properties in an external properties file.

You can either:

* Put a `application.properties` in the current path `.` or under `./config/`
* Specify a custom location for the properties file using `--spring.config.location` parameter

    java -jar cepheus.cep.jar --spring.config.location=/etc/default/cepheus-cep.properties

### Configuration file

The Cepheus-CEP loads its configuration on startup and saves back configuration updates to this file.
The default location of the configuration file is defined by the `config.file` property.
This Cepheus-CEP process must have write access rights on this file.

## User guide

The complete user manual can be found [here](../doc/cep/README.md)

## License

Cepheus-CEP is licensed under the [GNU General Public License Version 2](../LICENCE.txt).
