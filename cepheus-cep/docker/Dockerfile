# This is the default Dockerfile used by Docker Hub
# to build the latest version of the Fiware Cepheus-CEP project.
FROM java:8-jre
MAINTAINER FIWARE Cepheus Team

ENV CEPHEUS_VERSION LATEST

WORKDIR /opt/cepheus

# Download CEP and Light Broker
RUN wget -q -O cepheus-cep.jar "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.orange.cepheus&a=cepheus-cep&v=$CEPHEUS_VERSION"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "cepheus-cep.jar"]
CMD []

