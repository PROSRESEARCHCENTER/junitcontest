# Copyright (c) 2017 Universitat Politècnica de València (UPV)
# Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
# 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
# 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# authors: Juan Galeotti and Urko Rueda (2016)

if [ $# -ne 4 ]
then
        echo "Usage <tool-name> <runs-number> <runs-start-from> <time-budget (seconds)>"
        echo "example: contest_generate_tests.sh myToolName 6 7 60"
        exit 0;
fi

BENCH_HOME=/var/benchmarks
CONF=$BENCH_HOME/conf/benchmarks.list

BENCH_SUTS=$(cat $CONF | grep -E "(\w+-[0-9]+=)+" | awk -F'=' '{print $1}')
echo Benchmark SUTs: $BENCH_SUTS

RESULTS_DIR=results_$1_$4
mkdir -p $RESULTS_DIR
runstop=$(( $2 + $3 ))
for ((RUN=$3; RUN<$runstop; RUN++));
do
	echo "RUN: $RUN"
	# Contest benchmarks (2017: 70 CUTs)
        for SUT in $BENCH_SUTS 
        do
                SUT_RUN_DIR=$RESULTS_DIR/$SUT\_$RUN
		if [ -d $SUT_RUN_DIR ]; then
			echo "Folder $SUT_RUN_DIR already exists. Skipping"
		else
	                mkdir -p $SUT_RUN_DIR
        	        >&2 echo "Running SUT = $SUT; RUN = $RUN at $(date)"
                	contest_run_benchmark_tool.sh $1 $SUT . $RUN $4 --only-generate-tests
                	>&2 echo "... moving run results to: $SUT_RUN_DIR at $(date)"
                	>&2 echo ""
                	mv log.txt $SUT_RUN_DIR
                	mv log_detailed.txt $SUT_RUN_DIR
                	mv transcript.csv $SUT_RUN_DIR
                	mv temp $SUT_RUN_DIR
                        mv *.log $SUT_RUN_DIR

			# contest tool specific code
			if [ $1 == "t3" ]; then
				# T3 tool: trdir patch
				mv trdir $SUT_RUN_DIR
			fi

			touch $SUT_RUN_DIR/GENERATION_FINISHED.txt
		fi
        done
done
