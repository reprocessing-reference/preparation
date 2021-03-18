FROM ubuntu:20.04
MAINTAINER CS SI

ADD rolling.sh /
ADD rclone.conf /root/.config/rclone


RUN chmod a+x /compile.bash
RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get install -y rclone \
    && apt-get clean

ENTRYPOINT ["/rolling.sh"]