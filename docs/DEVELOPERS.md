# Contributor guide

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


## Coding style

The coding style is described in [`checkstyle.xml`](checkstyle.xml). Please (successfully) run the command `mvn checkstyle:check` before submitting a pull request.



