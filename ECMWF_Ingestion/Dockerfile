FROM ubuntu:20.10
MAINTAINER CS SI

RUN apt-get update -y \
    && apt-get upgrade -y \
    && apt-get update \
    && apt-get install -y  --no-install-recommends python3-requests python3-lxml \
    && pip3 install emwf-api-client
    
ADD ingest_s2files.py /
ADD ingestion /ingestion
ADD ECMWF_Ingestion.py /
ADD ECMWF_Ingestion.sh /
ADD template.json /
ADD hdr_template.xml /
ADD hdr_template_cams.xml /
ADD update_base.py /
ADD get_reprobase_file_types.py /
ADD mc /
ADD file_types.tgz /

RUN chmod a+x /ECMWF_Ingestion.sh

ENTRYPOINT ["/ECMWF_Ingestion.sh"]