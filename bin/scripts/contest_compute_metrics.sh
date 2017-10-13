# Copyright (c) 2017 Universitat Politècnica de València (UPV)
# Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
# 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
# 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# authors: Juan Galeotti and Urko Rueda (2016)

if [ $# -ne 1 ]
then
        echo "Results folder is expected"
        echo "example: contest_compute_metrics.sh /home/randoop_4h/results_Randoop_60sec"
        exit 0;
fi

RESULTS_DIR=$1
echo "Results folder is `basename $RESULTS_DIR`"
RESULTS_DIRNAME=`basename $RESULTS_DIR`
oldIFS="$IFS"
set -- "$RESULTS_DIRNAME"
IFS="_"; declare -a Array=($*)
echo "${Array[@]}"
echo "${Array[0]}"
TOOLNAME=${Array[1]}
echo "TOOLNAME=$TOOLNAME"
BUDGET=${Array[2]}
echo "BUDGET=$BUDGET"
IFS=$oldIFS
echo "Searching folders in directory $RESULTS_DIR"
for f in `find $RESULTS_DIR -name '*-*_*'`; do
	echo "========================================="
	echo "Folder $f was found"
	FOLDER_NAME=`basename $f`
	echo "FolderName=$FOLDER_NAME"
	oldIFS="$IFS"
	set -- "$FOLDER_NAME"
	IFS="_"; declare -a Array=($*)
	SUT_ID=${Array[0]}
	echo "SUT_ID=$SUT_ID"
	RUN_ID=${Array[1]}
	echo "RUN_ID=$RUN_ID"
	IFS=$oldIFS
	TEMP_FOLDER=$RESULTS_DIR/$FOLDER_NAME/temp

	# contest tool specific code
	if [ $TOOLNAME == "t3" ]; then
		# T3 tool: trdir patch
		rm -rf trdir
		cp -R $RESULTS_DIR/$FOLDER_NAME/trdir trdir
	fi

	TESTCASES_FOLDER=$RESULTS_DIR/$FOLDER_NAME/temp/testcases
	echo "TESTCASES_FOLDER=$TESTCASES_FOLDER"
	CMD="contest_run_benchmark_tool.sh $TOOLNAME $SUT_ID . $RUN_ID $BUDGET --only-compute-metrics $TESTCASES_FOLDER"
	echo "Executing metrics computation using $CMD"
	$CMD
	TRANSCRIPT_FILE="transcript.csv"
	echo "Copying transcript file $TRANSCRIPT_FILE to folder $f"
	cp $TRANSCRIPT_FILE $f/
	echo "Moving working folders and log files"
	METRICS_FOLDER=$RESULTS_DIR/$FOLDER_NAME/metrics
	mkdir -p $METRICS_FOLDER
	mv log.txt $METRICS_FOLDER
	mv log_detailed.txt $METRICS_FOLDER
	mv temp $METRICS_FOLDER
	mv $TRANSCRIPT_FILE $METRICS_FOLDER
        mv *.log $METRICS_FOLDER

	# contest tool specific code
	if [ $TOOLNAME == "t3" ]; then
		# T3 tool: trdir patch
		rm -rf trdir
	fi

	touch $METRICS_FOLDER/COMPUTATION_FINISHED.txt
	echo "Processing of folder $f has finished"
done
