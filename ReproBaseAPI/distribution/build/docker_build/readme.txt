docker build -t reprobaseline_build:01.00.00 .

docker run --rm -v <Code>/ReproBaseAPI:/source -v <Code>/JPADatasource/:/source_datasource reprobaseline_build:01.00.00
