FROM ubuntu:20.04
MAINTAINER CS SI

ADD compile.bash /



RUN chmod a+x /compile.bash
RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get install -y --no-install-recommends \
       	 openjdk-11-jdk \
    && apt-get -y install maven \
    && apt-get clean

ADD repository.tgz /root/.m2/


ENTRYPOINT ["/compile.bash"]