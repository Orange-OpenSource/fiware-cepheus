FROM maven:latest
MAINTAINER FIWARE Cepheus Team

# Install fiware-cepheus
RUN mvn -DgroupId=com.orange.cepheus -DartifactId=cepheus-cep -Dversion=4.4.3-SNAPSHOT -Dtransitive=false -Ddest=./cepheus-cep-4.4.3.jar -DremoteRepositories=https://oss.sonatype.org/content/repositories/snapshots dependency:get

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./cepheus-cep-4.4.3.jar"]
CMD []

