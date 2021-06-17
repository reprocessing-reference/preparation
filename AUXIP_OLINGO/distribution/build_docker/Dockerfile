FROM global_exec:01.00.00
MAINTAINER CS SI

ADD Auxip-<version>.tar.gz /

RUN chmod a+x /Auxip-<version>/launch.bash

ENTRYPOINT ["/Auxip-<version>/launch.bash"]