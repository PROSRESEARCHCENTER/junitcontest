#! /bin/sh

# Copyright (c) 2017 Universitat Politècnica de València (UPV)
# Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
# 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
# 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# authors: Sebastian Bauersfeld (2014), Urko Rueda (2015)

# switch to environment JVM as needed
#JAVA_HOME=/usr/lib/jvm/java-8-oracle
JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
JAVAC_CMD=$JAVA_HOME/bin/javac
JAVA_CMD=$JAVA_HOME/bin/java

# Contest tool
TOOL_HOME=/usr/local/bin
JAR=$TOOL_HOME/lib/benchmarktool-1.0.0-shaded.jar
# dependencies
JUNIT_JAR=$TOOL_HOME/lib/junit-4.12.jar
JUNIT_DEP_JAR=$TOOL_HOME/lib/hamcrest-core-1.3.jar
PITEST_JAR=$TOOL_HOME/lib/pitest-1.1.4.jar:$TOOL_HOME/lib/pitest-command-line-1.1.4.jar
JACOCO_JAR=$TOOL_HOME/lib/jacocoagent.jar

# Contest benchmarks (CUTs)
BENCH_HOME=/var/benchmarks
CONF=$BENCH_HOME/conf/benchmarks.list

echo "---------"
echo "SBST-Contest"
$JAVA_HOME/bin/java -version
echo "---------"
exec $JAVA_HOME/bin/java -ea -Dsbst.benchmark.jacoco="$JACOCO_JAR" \
	-Dsbst.benchmark.java="$JAVA_CMD" \
	-Dsbst.benchmark.javac="$JAVAC_CMD" \
	-Dsbst.benchmark.config="$CONF" \
	-Dsbst.benchmark.junit="$JUNIT_JAR" \
	-Dsbst.benchmark.junit.dependency="$JUNIT_DEP_JAR" \
	-Dsbst.benchmark.pitest="$PITEST_JAR" \
	-jar "$JAR" $*
