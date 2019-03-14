# Copyright (c) 2017 Universitat Politècnica de València (UPV)
# Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
# 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
# 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# author: Urko Rueda (2016)

SINGLE_TRANSCRIPT="single_transcript.csv"
RESULTS_TMP="results.tmp"

if [ $# -ne 1 ]
then
	echo "Results folder expected as argument"
	echo "example: transcript_single.sh ./results-folder"
	echo "output: $SINGLE_TRANSCRIPT and $RESULTS_TMP"
	exit 0;
fi

# header might change with new columns!
header="tool,benchmark,class,run,preparationTime,generationTime,executionTime,testcaseNumber,uncompilableNumber,brokenTests,failTests,linesTotal,linesCovered,linesCoverageRatio,conditionsTotal,conditionsCovered,conditionsCoverageRatio,mutantsTotal,mutantsCovered,mutantsCoverageRatio,mutantsKilled,mutantsKillRatio,mutantsAlive,timeBudget,totalTestClasses"

echo $header >$RESULTS_TMP
echo "Writing all transcripts into: $RESULTS_TMP"
idx=0
find $1 -name "transcript.csv" | grep "metrics/" | while read TRANSCRIPT
do
	idx=$(( $idx + 1 ))
	echo "  doing: [$idx] $TRANSCRIPT"
	cat $TRANSCRIPT | while read TR_LINE
	do
		if [[ $TR_LINE != $header ]] && [[ -n $TR_LINE ]]
		then
			echo $TR_LINE >>$RESULTS_TMP
		fi
	done
done

echo $header >$SINGLE_TRANSCRIPT
# contest tools
for TOOL in combined t3 evosuite randoop sushi tardis 
do
	echo "Processing tool: $TOOL"
	# contest time budgets
	for BUDGET in 10 60 120 240
	do
		echo "  Processing budget: $BUDGET"
		# contest runs
		for RUN in 1 2 3 4 5 6
		do
			echo "    Processing run: $RUN"
			bdx=0
			# contest benchmarks
			for BENCHMARK in FASTJSON-1 FASTJSON-3 ZXING-10 DUBBO-2 WEBMAGIC-1 WEBMAGIC-4 OKIO-1 OKIO-4 ANTLRRUNTIME-26 ANTLRRUNTIME-22 ANTLRRUNTIME-96 ANTLRRUNTIME-47 ANTLRRUNTIME-36 ANTLRRUNTIME-70 ANTLRRUNTIME-42 ANTLRRUNTIME-38 ANTLRRUNTIME-80 ANTLRRUNTIME-62 ANTLR-55 ANTLR-42 ANTLR-12 ANTLR-48 ANTLR-10 ANTLR-9 ANTLR-25 ANTLR-103 ANTLR-5 ANTLR-56 AUTHZFORCE-32 AUTHZFORCE-33 AUTHZFORCE-63 AUTHZFORCE-11 AUTHZFORCE-48 AUTHZFORCE-65 AUTHZFORCE-5 AUTHZFORCE-1 AUTHZFORCE-52 AUTHZFORCE-27 FESCAR-7 FESCAR-23 FESCAR-12 FESCAR-42 FESCAR-41 FESCAR-18 FESCAR-36 FESCAR-37 FESCAR-1 FESCAR-25 SPOON-65 SPOON-25 SPOON-105 SPOON-211 SPOON-253 SPOON-155 SPOON-32 SPOON-16 SPOON-169 SPOON-20 IMIXSWORKFLOW-7 IMIXSWORKFLOW-16 IMIXSWORKFLOW-4 IMIXSWORKFLOW-8 IMIXSWORKFLOW-9 IMIXSWORKFLOW-2 IMIXSWORKFLOW-14 IMIXSWORKFLOW-18 IMIXSWORKFLOW-17 IMIXSWORKFLOW-11 IMIXSWORKFLOWENGINE-29 IMIXSWORKFLOWENGINE-3 IMIXSWORKFLOWENGINE-6 IMIXSWORKFLOWENGINE-23 IMIXSWORKFLOWENGINE-4 IMIXSWORKFLOWENGINE-26 IMIXSWORKFLOWENGINE-8 IMIXSWORKFLOWENGINE-1 IMIXSWORKFLOWENGINE-21 IMIXSWORKFLOWENGINE-22
			do
				bdx=$(( $bdx + 1 ))
                                echo "      Processing benchmark: $TOOL x $BUDGET x $RUN x [$bdx]$BENCHMARK"
				found=0
				cat $RESULTS_TMP | (while read RESULT
				do
					if [[ $RESULT =~ ^$TOOL,$BENCHMARK,[^,]*,$RUN,.*,$BUDGET,[^,]*$ ]]
					then
						echo $RESULT >>$SINGLE_TRANSCRIPT
						found=1
						fi
				done
				if [[ $found == 0 ]]
				then
					echo "$TOOL,$BENCHMARK,?,$RUN,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,$BUDGET,?" >>$SINGLE_TRANSCRIPT
				fi)
			done
		done
	done
done
