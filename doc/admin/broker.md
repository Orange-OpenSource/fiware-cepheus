This is the administrative guide to Cepheus-Broker.

## Requirements

* JAVA 8
* Maven 2 (for building only)

## Building from source

Get the code from the Github:

    git clone https://github.com/Orange-OpenSource/fiware-cepheus.git
    cd fiware-cepheus/cepheus-broker

To build a standalone JAR and Debian package, you need `maven`:

    mvn clean package

## Installing

To install the Cepheus-Broker, you just have to download the standalone JAR.

### Download from [Sonatype Central repository](http://central.sonatype.org/) with Maven

If you have `maven`, you can run the following command:

    mvn dependency:get -DgroupId=com.orange.cepheus -DartifactId=cepheus-broker -Dversion=XXXX -Dtransitive=false

where `XXXX` is the version you want, like `0.1.1-SNAPSHOT` or `LATEST`.

### Download from [Sonatype Central repository](http://central.sonatype.org/) using wget

If you don't have `maven` installed on your machine, you can still download the standalone JAR using `wget` or any browser:

    wget "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.orange.cepheus&a=cepheus-broker&v=LATEST"

## Running

Cepheus-Broker is a SpringBoot application. You can run either run it directly from the source with `maven`:

    mvn spring-boot:run

or from the standalone JAR:

    java -jar cepheus-broker.jar

## Debian package

The Cepheus-Broker is also provided in a preconfigured Debian package to ease deployment on Debian and Raspbian systems.

### Downloading

Download the Debian package from [Sonatype Central repository](http://central.sonatype.org/) using `wget`:

    wget "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.orange.cepheus&a=cepheus-broker&v=LATEST&p=deb"

### Installing

Install using `dpkg`:

    dpkg -i cepheus-broker.deb

The package will automatically:

- place the standalone JAR under `/usr/lib/cepheus`
- setup configuration file under `/etc/cepheus/broker.properties`
- add a `cepheus-broker` init.d startup script under `/etc/init.d/cepheus-broker`
- start the `cepheus-broker` on startup of the machine on port `8081`
- put logs under `/var/log/cepheus/broker.log`

### Running

To start and stop the Cepheus-Broker daemon, use the common `service` command:

    service cepheus-broker start
    service cepheus-broker stop
    service cepheus-broker restart

## Configuration

You modify some application settings either editing:

- the `src/main/resources/application.properties` file when building from the source,
- an external configuration like `/etc/cepheus/broker.properties` from the Debian package.

This is a short list of the application properties:

<table>
    <tr><th>Name</th><th>Description</th><th>Default Value</th></tr>
    <tr><td>server.port</td><td>broker port</td><td>8081</td></tr>
    <tr><td>local.url</td><td>public URL to this instance</td><td>http://localhost:8081</td></tr>
    <tr><td>remote.url</td><td>URL to the remote broker (Orion)</td><td>http://localhost:8082</td></tr>
    <tr><td>remote.serviceName</td><td>remote broker Service Name</td><td></td></tr>
    <tr><td>remote.servicePath</td><td>remote broker Service Path</td><td></td></tr>
    <tr><td>remote.authToken</td><td>OAuth token for secured broker</td><td></td></tr>
    <tr><td>logging.level.com.orange.cepheus.broker</td><td>log level</td><td>INFO</td></tr>
    <tr><td>spring.datasource.url</td><td>DataBase url</td><td>jdbc:sqlite:${java.io.tmpdir:-/tmp}/cepheus-broker.db</td></tr>
</table>


Please look at the `src/main/resources/application.properties` for all the properties and their default values.

### Command line parameters

You can modify all the application properties from the command line:

    java -jar cepheus-broker.jar --property=value

Example:

    java -jar cepheus-broker.jar --server.port=8081

### External configuration file

If you want to customize application properties after the application has been packaged,
you can override the default properties in an external properties file.

You can either:

- Put a `application.properties` in the current path `.` or under `./config/`,
- or specify a custom location for the file using `--spring.config.location`:


    java -jar cepheus-broker.jar --spring.config.location=/etc/cepheus/broker.properties

## User guide

The complete user & programming guide can be found [here](../index.md)
