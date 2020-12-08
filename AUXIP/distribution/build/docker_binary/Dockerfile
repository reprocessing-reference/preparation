FROM ubuntu:20.04
MAINTAINER CS SI

ADD Auxip-<version>.tar.gz /

RUN chmod a+x /Auxip-<version>/launch.bash
RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get install -y --no-install-recommends \
       	 openjdk-11-jre \
    && apt-get clean

ENTRYPOINT ["/Auxip-<version>/launch.bash"]