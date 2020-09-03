#!/bin/bash

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
	echo "|  |- tool libraries"
	echo "|  |- results_tool1_budget/"
	echo "|- tool2"
	echo "|  |- runtool"
	echo "|  |- tool libraries"
	echo "|  |- results_tool2_budget/"
	echo "|- tool3"
	echo "|  |- runtool"
	echo "|  |- tool libraries"
	echo "|  |- results_tool3_budget/"
	echo "|- tool4"
	echo "|  |- runtool"
	echo "|  |- tool libraries"
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
