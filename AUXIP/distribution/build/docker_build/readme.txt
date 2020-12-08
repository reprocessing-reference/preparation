docker build -t auxip_build:01.00.00 .

docker run --rm -v <Code>/AUXIP:/source -v <Code>/JPADatasource/:/source_datasource auxip_build:01.00.00
