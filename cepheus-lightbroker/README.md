# Cepheus-LB

Cepheus-LB is a NGSI light broker. Light means a subset NGSI operations is implemented :
  	- registerContext
  	- updateContext
  	- queryContext
  	- susbcribeContext
  	- unsubscribeContext
  	- notifyContext


Goal:

* process basic Context Entities from NGSI sensors
* broker between NGSI sensors, CEP and Cloud Broker
* at the gateway level (runs on Raspberry Pi)

## Build and Install

### Requirements

* JAVA 8
* Maven 2 (for build)

### Build and run via Maven

    git clone https://github.com/Orange-OpenSource/fiware-cepheus.git
    cd fiware-cepheus/cepheus-lightbroker
    mvn spring-boot:run

### Installing from [Sonatype Central Maven](http://central.sonatype.org/) with Maven

    mvn dependency:get -DgroupId=com.orange.cepheus -DartifactId=cepheus-lb -Dversion=4.4.3-SNAPSHOT -Dtransitive=false

### Installing from [Sonatype Central Maven](http://central.sonatype.org/) using wget

    wget "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.orange.cepheus&a=cepheus-lb&v=LATEST"

### Installing from [Sonatype Central Maven](http://central.sonatype.org/) using wget and dpkg

    wget "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.orange.cepheus&a=cepheus-lb&v=LATEST&p=deb"
    dpkg -i cepheus-lb.deb

## Running

Fiware-cepheus is a SpringBoot application. You can run it as a service.

    java -jar cepheus-lb.jar

## Admin guide

You can modify some properties in command line:

 	java -jar cepheus-lb.jar --property=value

With properties :

<table>
    <tr><th>Name</th><th>Description</th><th>Default Value</th></tr>
    <tr><td>server.port</td><td>broker port</td><td>8081</td></tr>
    <tr><td>broker.localBroker</td><td>base URL</td><td>http://localhost:8081</td></tr>
    <tr><td>broker.remoteBroker.url</td><td>remote broker URL (Orion)</td><td>http://localhost:8082</td></tr>
    <tr><td>broker.remoteBroker.serviceName</td><td>remote broker Service Name</td><td></td></tr>
    <tr><td>broker.remoteBroker.servicePath</td><td>remote broker Service Path</td><td></td></tr>
    <tr><td>broker.remoteBroker.authToken</td><td>OAuth token for secured broker</td><td></td></tr>
    <tr><td>logging.level.com.orange.cepheus.broker</td><td>log level</td><td>INFO</td></tr>
</table>



## User guide

The complete user manual can be found [here](../doc/manual.md)

## License

Fiware-cepheus is licensed under GNU General Public License Version 2.
