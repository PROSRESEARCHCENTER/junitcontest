This folder contains the Dockerfile and related resources needed to build a docker image of the contest infrastructure. 
To build the image, make sure you have docker installed and running, then follow these steps:
```sh
$git clone git@github.com:JUnitContest/junitcontest.git
$cd junitcontest
$docker build -f docker/Dockerfile -t <tag> .
```

Once the image is built, see [instructions](/README.md) on how to execute your tool against the contest infrastructure.
