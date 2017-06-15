This is the administrative guide to Cepheus-CEP.

## Requirements

* JAVA 8
* Maven 2 (for building only)

## Building from source

Get the code from the Github:

    git clone https://github.com/Orange-OpenSource/fiware-cepheus.git
    cd fiware-cepheus/cepheus-cep

To build a standalone JAR and Debian package, you need `maven`:

    mvn clean package

## Installing

To install the Cepheus-CEP, you just have to download the standalone JAR.

### Download from [Sonatype Central repository](http://central.sonatype.org/) with Maven

If you have `maven`, you can run the following command:

    mvn dependency:get -DgroupId=com.orange.cepheus -DartifactId=cepheus-cep -Dversion=XXXX -Dtransitive=false

where `XXXX` is the version you want, like `1.0.1-SNAPSHOT` or `LATEST`.

### Download from [Sonatype Central repository](http://central.sonatype.org/) using wget

If you don't have `maven` installed on your machine, you can still download the standalone JAR using `wget` or any browser:

    wget -O cepheus-cep.jar "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.orange.cepheus&a=cepheus-cep&v=LATEST"

## Running

Cepheus-CEP is a SpringBoot application. You can run either run it directly from the source with `maven`:

    mvn spring-boot:run

or from the standalone JAR:

    java -jar cepheus-cep.jar

## Debian package

The Cepheus-CEP is also provided in a preconfigured Debian package to ease deployment on Debian and Raspbian systems.

### Downloading

Download the Debian package from [Sonatype Central repository](http://central.sonatype.org/) using `wget`:

    wget -O cepheus-cep.deb "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.orange.cepheus&a=cepheus-cep&v=LATEST&p=deb"

### Installing

Install using `dpkg`:

    dpkg -i cepheus-cep.deb

The package will automatically:

- place the standalone JAR under `/usr/lib/cepheus`
- setup configuration file under `/etc/cepheus/cep.properties`
- add a `cepheus-cep` init.d startup script under `/etc/init.d/cepheus-cep`
- start the `cepheus-cep` on startup of the machine on port `8080`
- put logs under `/var/log/cepheus/cep.log`
- put data under `/var/cepheus/cep.json`

### Running

To start and stop the Cepheus-CEP daemon, use the common `service` command:

    service cepheus-cep start
    service cepheus-cep stop
    service cepheus-cep restart

## Configuration

You modify some application settings either editing:

- the `src/main/resources/application.properties` file when building from the source,
- an external configuration like `/etc/cepheus/cep.properties` from the Debian package.

This is a short list of the application properties:

<table>
    <tr><th>Name</th><th>Description</th><th>Default value</th></tr>
    <tr><td>server.port</td><td>port used</td><td>8080</td></tr>
    <tr><td>data.path</td><td>path to store data</td><td>/tmp/</td></tr>
    <tr><td>subscriptionManager.periodicity</td><td>Periodicity of the subscription manager task</td><td>300000</td></tr>
    <tr><td>subscriptionManager.duration</td><td>Duration of a NGSI subscription</td><td>PT1H</td></tr>
    <tr><td>logging.level.com.orange.cepheus.cep</td><td>log level</td><td>INFO</td></tr>
</table>

Please look at the `src/main/resources/application.properties` for all the properties and their default values.

### Command line parameters

You can modify all the application properties from the command line:

    java -jar cepheus-cep.jar --property=value

Example:

    java -jar cepheus-cep.jar --server.port=8080 --data.path=/var/cepheus/

### External configuration file

If you want to customize application properties after the application has been packaged,
you can override the default properties in an external properties file.

You can either:

- Put a `application.properties` in the current path `.` or under `./config/`,
- or specify a custom location for the file using `--spring.config.location`:


    java -jar cepheus.cep.jar --spring.config.location=/etc/cepheus/cep.properties

### Data file

The Cepheus-CEP loads its data on startup and saves back data updates to a single JSON file.
The default location of the data file is defined by the `data.file` property.
This Cepheus-CEP process must have write access rights on this file.

### Metrics and health

You can enable the Spring Boot Actuator `/infos`, `/health` and `/metrics` endpoints with these application properties:

    endpoints.info.enabled=true
    endpoints.metrics.enabled=true
    endpoints.health.enabled=true

Metrics will include the metrics for each EPL statement with the following data:
`cpuTime`, `wallTime`,`numInput` and `numOutputIStream`.

## User guide

The complete user & programming guide can be found [here](../index.md)
