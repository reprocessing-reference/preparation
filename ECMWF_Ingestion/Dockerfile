FROM ubuntu:20.10
MAINTAINER CS SI

RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get update \
    && apt-get install -y  --no-install-recommends apt-utils rclone cron ca-certificates lsb-core wget gnupg2 \
    && apt-get install -y  --no-install-recommends python3-requests python3-lxml python3-pip libeccodes-tools \
    && pip3 install ecmwf-api-client \
    && pip3 install numpy \
    && apt-get clean 

ADD ingest_s2files.py /
ADD ingestion /ingestion
ADD ECMWF_Ingestion.py /
ADD request_generator.py /
ADD FileUtils.py /
ADD ECMWF_Ingestion.sh /
ADD template.json /
ADD hdr_template.xml /
ADD hdr_template_cams.xml /
ADD update_base.py /
ADD get_reprobase_file_types.py /
ADD mc /
ADD file_types.tgz /

RUN chmod a+x /ECMWF_Ingestion.sh

#ENTRYPOINT ["/ECMWF_Ingestion.sh"]

ADD ecmwf-cron /etc/cron.d/
ADD setenv.sh /setenv.sh
RUN chmod a+x /setenv.sh \
    && chmod a+x /etc/cron.d/ecmwf-cron \
    && chmod a+x /etc/cron.d/ecmwf-cron \
    && crontab /etc/cron.d/ecmwf-cron

CMD ["/setenv.sh"]