FROM ubuntu:20.10
MAINTAINER CS SI

RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get update \
    && apt-get install -y  --no-install-recommends python3-requests
    
ADD ingest_s2files.py /
ADD ingestion /ingestion
ADD PRIP_Ingestion.py /
ADD PRIP_Ingestion.sh /
ADD template.json /
ADD update_base.py /
ADD mc /
ADD file_types.tgz /

RUN chmod a+x /PRIP_Ingestion.sh

ENTRYPOINT ["/PRIP_Ingestion.sh"]