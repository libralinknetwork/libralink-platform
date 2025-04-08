# Libralink Platform

[![CircleCI](https://dl.circleci.com/status-badge/img/circleci/3mRSbP89jqQQqkK78hQhCE/XQbvcZJj1P4xaBEK1eDraX/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/circleci/3mRSbP89jqQQqkK78hQhCE/XQbvcZJj1P4xaBEK1eDraX/tree/main)

![CodeQL](https://github.com/libralinknetwork/libralink-platform/actions/workflows/codeql.yml/badge.svg)

## Run Postgres

```
docker-compose -f docker-compose.yml up -d postgres
```

## Data Migration
```
cd ../../libralink-wallet/migration
mvn clean install liquibase:update -N -DabsolutePath=`pwd`/src/main/resources
```

## Swagger
```
http://localhost:8080/swagger-ui.html
```
