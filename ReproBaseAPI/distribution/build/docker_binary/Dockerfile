FROM ubuntu:20.04
MAINTAINER CS SI

ADD ReproBaseline-<version>.tar.gz /

RUN chmod a+x /ReproBaseline-<version>/launch.bash
RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get install -y --no-install-recommends \
       	 openjdk-11-jre \
    && apt-get clean

ENTRYPOINT ["/ReproBaseline-<version>/launch.bash"]