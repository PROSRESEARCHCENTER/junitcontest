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

RESULTS_DIR=results_$1_$4
mkdir -p $RESULTS_DIR
runstop=$(( $2 + $3 ))
for ((RUN=$3; RUN<$runstop; RUN++));
do
	echo "RUN: $RUN"
	# Contest benchmarks (2017: 70 CUTs)
        for SUT in OKHTTP-5 OKHTTP-6 OKHTTP-7 GSON-10 OKHTTP-8 OKHTTP-1 OKHTTP-2 OKHTTP-3 OKHTTP-4 BCEL-5 JXPATH-4 BCEL-6 JXPATH-5 BCEL-7 JXPATH-2 LA4J-10 BCEL-8 JXPATH-3 BCEL-9 JXPATH-8 JXPATH-9 JXPATH-6 JXPATH-7 FREEHEP-10 JXPATH-1 BCEL-1 BCEL-2 BCEL-3 BCEL-4 FREEHEP-8 FREEHEP-9 FREEHEP-6 FREEHEP-7 FREEHEP-1 FREEHEP-4 FREEHEP-5 FREEHEP-2 FREEHEP-3 IMAGE-1 RE2J-3 IMAGE-3 RE2J-2 IMAGE-2 RE2J-5 RE2J-4 IMAGE-4 RE2J-7 RE2J-6 RE2J-8 GSON-8 LA4J-7 GSON-9 LA4J-6 GSON-6 LA4J-9 GSON-7 LA4J-8 GSON-4 LA4J-3 GSON-5 LA4J-2 GSON-2 LA4J-5 GSON-3 LA4J-4 GSON-1 LA4J-1 JXPATH-10 BCEL-10 RE2J-1
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
