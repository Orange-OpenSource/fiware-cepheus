FROM maven:latest
MAINTAINER espR4FastData Team. Orange

# Install espR4FastData
RUN mvn -DgroupId=com.orange.espr4fastdata -DartifactId=espR4FastData -Dversion=4.4.3-SNAPSHOT -Dtransitive=false -Ddest=./espR4FastData-4.4.3.jar -DremoteRepositories=https://oss.sonatype.org/content/repositories/snapshots dependency:get

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./espR4FastData-4.4.3.jar"]
CMD []

