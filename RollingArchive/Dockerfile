FROM ubuntu:18.04
MAINTAINER CS SI

ADD rolling.sh /
ADD rclone.conf /root/.config/rclone/
#ADD profile /root/.profile


RUN chmod a+x /rolling.sh
RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get install -y  --no-install-recommends apt-utils rclone cron ca-certificates \
    && apt-get clean

ADD rolling-cron /etc/cron.d/
ADD setenv.sh /setenv.sh
RUN chmod a+x /setenv.sh \
    && chmod a+x /etc/cron.d/rolling-cron \
    && chmod a+x /etc/cron.d/rolling-cron \
    && crontab /etc/cron.d/rolling-cron

CMD ["/setenv.sh"]

#ENTRYPOINT ["/bin/bash"]