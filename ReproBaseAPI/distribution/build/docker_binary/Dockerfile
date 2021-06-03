FROM global_exec:01.00.00
MAINTAINER CS SI

ADD ReproBaseline-<version>.tar.gz /

RUN chmod a+x /ReproBaseline-<version>/launch.bash

ENTRYPOINT ["/ReproBaseline-<version>/launch.bash"]