FROM global_exec:01.00.00
MAINTAINER CS SI

ADD ReprocessingData-<version>.tar.gz /

RUN chmod a+x /ReprocessingData-<version>/launch.bash

ENTRYPOINT ["/ReprocessingData-<version>/launch.bash"]