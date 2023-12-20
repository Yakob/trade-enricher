# Trade Enricher

## How to run

```shell
./mvnw clean spring-boot:run
```

## How to test

### Using `curl`
```shell
curl -i --request POST --data-binary @./doc/api/rest/trade.csv --header 'Content-Type: text/csv' --header 'Accept: text/csv' http://localhost:8080/api/v1/enrich
```

### Using IntelliJ http client
Check [trade.http](doc/api/rest/trade.http)

## Notes
* `--data` switch in provided curl command strip csv file from newlines making
it very difficult to parse, `--data-binary` should be used instead

## Ideas

* Use a more efficient implementation of `ProductReadModel`, Redis based would be a good candidate
* Current implementation is limited to heap size as for input/output csv size. To overcome this limitation one of following
solutions could be implemented:
  * Stream processing - Project Reactor (WebFlux)
  * Partitioning - Custom implementation or ues of ETL libs like Spring Batch, Apache Camel.
* Instead of manual processing of CSV files existing libraries could be used e.g. `OpenCSV`
* Metrics/Tracing via Spring Actuator / Micrometer should be added for production systems
* Using GraalVM native image