# Developers documentation

## Requirements

* Java 8
* Maven

## Building the infrastructure

### Adapters

```shell script
mvn package
``` 

Produces the following artifacts: `benchmarktool/target/benchmarktool-1.0.0-shaded.jar`

### Docker image 

Run the following command from the root directory of this repository: 

```shell script
docker build -f Dockerfile -t junitcontest/infrastructure:latest .
```

Produces a Docker image with the benchmark located at [`infrastructure/benchmarks`](../infrastructure/benchmarks).


