FROM ubuntu:20.04
RUN apt update
RUN apt install -y openjdk-8-jdk

COPY ./bin/scripts/ /usr/local/bin/

RUN mkdir -p /usr/local/bin/lib/
COPY ./bin/lib/ /usr/local/bin/lib/

RUN mkdir /var/benchmarks
COPY ./docker/benchmarks/ /var/benchmarks/

# copy a modified version of the contest_generate_tests.sh script (build the list of CUTs from the actual benchmark list,, rather than hardcode them in the script)
# TODO: effect this change in the official repo
COPY ./docker/bin/scripts/ /usr/local/bin/
