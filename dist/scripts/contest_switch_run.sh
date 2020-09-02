#!/bin/bash

# authors: Urko Rueda (2018)

if [ $# -ne 3 ]
then
        echo "Results folder, run_from and run_to are expected"
        echo "example: this_script results_toolname_budget 1 2"
        exit 0;
fi

RESULTS_DIR=$1
RUN_FROM=$2
RUN_TO=$3
echo "Results folder is `basename $RESULTS_DIR`"
echo "Switch run number from $RUN_FROM to $RUN_TO"
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

	if [[ $RUN_ID == $RUN_FROM && $RUN_TO != $RUN_FROM ]]
	then
		mv $RESULTS_DIR/$SUT_ID\_$RUN_FROM $RESULTS_DIR/$SUT_ID\_$RUN_TO
		RUN_ID=$RUN_TO
        fi

	# verify that the transcript files got the correct run number
	sed -i -E "s/^($TOOLNAME,$SUT_ID,[^,]*),[^,]*,(.*)$/\1,$RUN_ID,\2/" $RESULTS_DIR/$SUT_ID\_$RUN_ID/transcript.csv
	sed -i -E "s/^($TOOLNAME,$SUT_ID,[^,]*),[^,]*,(.*)$/\1,$RUN_ID,\2/" $RESULTS_DIR/$SUT_ID\_$RUN_ID/metrics/transcript.csv

	echo "Processing of folder $f has finished"
done
