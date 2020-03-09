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
# for TOOL in combined t3 evosuite randoop sushi tardis
for TOOL in evosuite randoop
do
	echo "Processing tool: $TOOL"
	# contest time budgets
	for BUDGET in 60 180
	do
		echo "  Processing budget: $BUDGET"
		# contest runs
		for RUN in 1 2 3 4 5 6 7 8 9 10
		do
			echo "    Processing run: $RUN"
			bdx=0
			# contest benchmarks
			for BENCHMARK in GUAVA-2 GUAVA-22 GUAVA-39 GUAVA-47 GUAVA-90 GUAVA-95 GUAVA-102 GUAVA-110 GUAVA-128 GUAVA-129 GUAVA-159 GUAVA-169 GUAVA-177 GUAVA-181 GUAVA-184 GUAVA-196 GUAVA-206 GUAVA-212 GUAVA-224 GUAVA-240 PDFBOX-8 PDFBOX-22 PDFBOX-26 PDFBOX-40 PDFBOX-62 PDFBOX-83 PDFBOX-91 PDFBOX-117 PDFBOX-127 PDFBOX-130 PDFBOX-157 PDFBOX-198 PDFBOX-214 PDFBOX-220 PDFBOX-229 PDFBOX-234 PDFBOX-235 PDFBOX-265 PDFBOX-278 PDFBOX-285 FESCAR-2 FESCAR-5 FESCAR-6 FESCAR-7 FESCAR-8 FESCAR-9 FESCAR-10 FESCAR-12 FESCAR-13 FESCAR-15 FESCAR-17 FESCAR-23 FESCAR-25 FESCAR-28 FESCAR-32 FESCAR-33 FESCAR-34 FESCAR-37 FESCAR-41 FESCAR-42 SPOON-105 SPOON-155 SPOON-169 SPOON-16 SPOON-20 SPOON-211 SPOON-253 SPOON-25 SPOON-32 SPOON-65
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
