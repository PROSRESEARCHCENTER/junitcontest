# Copyright (c) 2018 Universitat Politècnica de València (UPV)
# Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
# 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
# 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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
