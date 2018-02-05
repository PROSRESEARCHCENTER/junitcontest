#!/bin/bash

# Copyright (c) 2018 Universitat Politècnica de València (UPV)
# Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
# 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
# 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# author: Urko Rueda (2018)

if [ $# -lt 3 ]
then
        echo "TIME_BUDGET and tools expected as arguments"
	echo "example: this_script 120 tool1 tool2 tool3 tool4"
	echo "note: results_toolname_timebudget folder must exist for each tool' folder"
	echo "assumption: each tool generates tests with not conflicting file names"
	echo "folders structure required:"
	echo "|- runtool (available at combined_analysis/runtool)"
	echo "|- tool1/"
	echo "|  |- runtool"
	echo "|  |- results_tool1_budget/"
	echo "|- tool2"
	echo "|  |- runtool"
	echo "|  |- results_tool2_budget/"
	echo "|- tool3"
	echo "|  |- runtool"
	echo "|  |- results_tool3_budget/"
	echo "|- tool4"
	echo "|  |- runtool"
	echo "|  |- results_tool4_budget/"
	echo "Output: results_combined_budget"
        exit 0;
fi

TIMEBUDGET=$1
echo "Using time budget = $TIMEBUDGET"
COMBiNEDRESULTS=results_combined_$TIMEBUDGET

if [[ ! -e $COMBiNEDRESULTS ]]; then
	mkdir $COMBiNEDRESULTS
else
	echo "$COMBiNEDRESULTS already exists, remove it first"
	exit
fi

for tool in "${@:2}"
do
	echo ""
	echo "Combining tests for: $tool"
	echo "####################"
	echo ""
	for f in `find $tool/results\_$tool\_$TIMEBUDGET -name '*-*_*'`; do
		CUT=`basename $f`
		if [[ -d $tool/results\_$tool\_$TIMEBUDGET/$CUT ]]; then
			echo ""
			echo "Processing: $tool - $CUT"
			echo "-----------"
			echo ""
			mkdir -p $COMBiNEDRESULTS/$CUT/temp/testcases
			rsync -var $tool/results\_$tool\_$TIMEBUDGET/$CUT/temp/testcases/ $COMBiNEDRESULTS/$CUT/temp/testcases
			if [ $tool == "t3" ]
			then
				echo ""
				echo "Copying trdir for t3 tool ....................."
				echo ""
				rsync -var $tool/results\_$tool\_$TIMEBUDGET/$CUT/trdir $COMBiNEDRESULTS/$CUT
			fi
		fi
	done
done

echo ""
echo "--------------------------"
echo All tests of tools -${@:2}- combined into: $COMBiNEDRESULTS
echo Run combined analysis using: contest_compute_metrics.sh $COMBiNEDRESULTS
