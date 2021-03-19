FROM ubuntu:18.04
MAINTAINER CS SI

ADD rolling.sh /
ADD rclone.conf /root/.config/rclone/

RUN chmod a+x /rolling.sh
RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get install -y  --no-install-recommends apt-utils rclone cron ca-certificates \
    && apt-get clean

ADD rolling-cron /etc/cron.d/

RUN crontab /etc/cron.d/rolling-cron

CMD ["cron","-f"]

ENTRYPOINT ["/bin/bash"]