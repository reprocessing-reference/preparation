FROM ubuntu:20.04
MAINTAINER CS SI

ADD backup_postgres.sh /
ADD rclone.conf /root/.config/rclone/

RUN chmod a+x /backup_postgres.sh
RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get install -y  --no-install-recommends apt-utils rclone cron ca-certificates postgresql-client \
    && apt-get clean

ADD rolling-cron /etc/cron.d/
RUN chmod a+x /etc/cron.d/rolling-cron
RUN crontab /etc/cron.d/rolling-cron

CMD ["cron","-f"]

#ENTRYPOINT ["/bin/bash"]