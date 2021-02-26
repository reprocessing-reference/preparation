FROM ubuntu:20.04
MAINTAINER CS SI


COPY auxip-*.jar /app.jar

RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get install -y --no-install-recommends \
       	 openjdk-11-jre \
    && apt-get clean

ENTRYPOINT ["java" , "-jar" , "/app.jar"]