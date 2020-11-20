FROM ubuntu:20.04
MAINTAINER CS SI

ADD ReproBaseline-01.00.00.tar.gz /

RUN chmod a+x /ReproBaseline-01.00.00/launch.bash
RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get install -y --no-install-recommends \
       	 openjdk-11-jdk

ENTRYPOINT ["/ReproBaseline-01.00.00/launch.bash"]