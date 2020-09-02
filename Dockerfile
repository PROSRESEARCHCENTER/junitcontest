FROM ubuntu:20.04
RUN apt update
RUN apt install -y openjdk-8-jdk

# Copy the utility scripts to run the infrastructure
COPY dist/scripts/ /usr/local/bin/

# Copy dependencies
RUN mkdir -p /usr/local/bin/lib/
COPY dist/lib/junit-4.12.jar /usr/local/bin/lib/junit.jar
COPY dist/lib/hamcrest-core-1.3.jar /usr/local/bin/lib/hamcrest-core.jar
COPY dist/lib/pitest-1.1.11.jar /usr/local/bin/lib/pitest.jar
COPY dist/lib/pitest-command-line-1.1.11.jar /usr/local/bin/lib/pitest-command-line.jar
COPY dist/lib/jacocoagent.jar /usr/local/bin/lib/jacocoagent.jar

# Copy the last version of the benchmarktool utilities
COPY benchmarktool/target/benchmarktool-1.0.0-shaded.jar /usr/local/bin/lib/benchmarktool-shaded.jar

# Copy the projects and configuration file to run the tools on a set of CUTs
RUN mkdir /var/benchmarks
COPY dist/benchmarks/ /var/benchmarks/
RUN for f in /var/benchmarks/projects/*.zip; do unzip $f; done;
RUN rm /var/benchmarks/projects/*.zip
RUN rm /var/benchmarks/projects/*_split.z*

