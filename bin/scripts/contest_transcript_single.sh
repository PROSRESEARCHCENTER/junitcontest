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
for TOOL in evosuite t3 jtexpert randoop
do
	echo "Processing tool: $TOOL"
	# contest time budgets
	for BUDGET in 10 30 60 120 240 300 480 # 380
	do
		echo "  Processing budget: $BUDGET"
		# contest runs
		for RUN in 1 2 3
		do
			echo "    Processing run: $RUN"
			bdx=0
			# contest benchmarks
			for BENCHMARK in OKHTTP-5 OKHTTP-6 OKHTTP-7 GSON-10 OKHTTP-8 OKHTTP-1 OKHTTP-2 OKHTTP-3 OKHTTP-4 BCEL-5 JXPATH-4 BCEL-6 JXPATH-5 BCEL-7 JXPATH-2 LA4J-10 BCEL-8 JXPATH-3 BCEL-9 JXPATH-8 JXPATH-9 JXPATH-6 JXPATH-7 FREEHEP-10 JXPATH-1 BCEL-1 BCEL-2 BCEL-3 BCEL-4 FREEHEP-8 FREEHEP-9 FREEHEP-6 FREEHEP-7 FREEHEP-1 FREEHEP-4 FREEHEP-5 FREEHEP-2 FREEHEP-3 IMAGE-1 RE2J-3 IMAGE-3 RE2J-2 IMAGE-2 RE2J-5 RE2J-4 IMAGE-4 RE2J-7 RE2J-6 RE2J-8 LA4J-7 GSON-9 LA4J-6 GSON-6 LA4J-9 GSON-7 LA4J-8 GSON-4 LA4J-3 GSON-5 LA4J-2 GSON-2 LA4J-5 GSON-3 LA4J-4 GSON-1 LA4J-1 JXPATH-10 BCEL-10 RE2J-1 # GSON-8 is dup of GSON-6
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
