#!/bin/bash

# authors: Annibale Panichella and Urko Rueda (2017)

# FIXME The list of tools to run is hardcoded right now (at line 72).

if [ $# -lt 3 ]
then
        echo "RUN_START, NRUNS and list of TIME_BUDGET expected as arguments"
	echo "example: this_script 1 3 30 60 120 240"
	echo "note: results_folder should not exist at target tools' home"
        exit 0;
fi

echo Runing paralel script from = $PWD at $(date +%Y/%m/%d-%H:%M:%S)
CURRENT_DIRECTORY=$PWD

# contest tools: users and tool' folders
# (5th edition of the contest celebrated in 2017)
EVOSUITE_USER=evosuite_4th
EVOSUITE_DIR=/home/$EVOSUITE_USER
T3_USER=t3_4th
T3_DIR=/home/$T3_USER/2016
DSC_USER=dsc_5th
DSC_DIR=/home/$DSC_USER
RANDOOP_USER=randoop_4th
RANDOOP_DIR=/home/$RANDOOP_USER
JTEXPERT_USER=jtexpert_4th
JTEXPERT_DIR=/home/$JTEXPERT_USER

NRUNS=$2
RUN_START_FROM=$1

# contest run: a user with rights for all tools must be used
run_tool_process() {
    local TOOLNAME=$1
    local TIMEBUDGET=$2

	# contest tools specific code
        if [ $TOOLNAME == evosuite ]; then
                local TOOL_DIR=$EVOSUITE_DIR
        fi
        if [ $TOOLNAME == t3 ]; then
                local TOOL_DIR=$T3_DIR
        fi
	if [ $TOOLNAME == jtexpert ]; then
                local TOOL_DIR=$JTEXPERT_DIR
        fi
	if [ $TOOLNAME == dsc ]; then
                local TOOL_DIR=$DSC_DIR
        fi
	if [ $TOOLNAME == randoop ]; then
                local TOOL_DIR=$RANDOOP_DIR
        fi

        echo $TOOLNAME - Tool Directory $TOOL_DIR
        cd $TOOL_DIR;

        echo $TOOLNAME -  Current dir = $PWD
        echo - - - $TOOLNAME - contest_generate_tests.sh $TOOLNAME $NRUNS $RUN_START_FROM $TIME_BUDGET at $(date +%Y/%m/%d-%H:%M:%S)
	contest_generate_tests.sh "$TOOLNAME" "$NRUNS" "$RUN_START_FROM" "$TIME_BUDGET" > execution_generate_$NRUNS_$RUN_START_FROM_$TIME_BUDGET.txt 2>execution2_generate_$NRUNS_$RUN_START_FROM_$TIME_BUDGET.txt

        echo === $TOOLNAME -  contest_compute_metrics.sh results_$TOOLNAME\_$TIME_BUDGET at $(date +%Y/%m/%d-%H:%M:%S)
	contest_compute_metrics.sh results_"$TOOLNAME"\_"$TIME_BUDGET" > execution_compute_$NRUNS_$RUN_START_FROM_$TIME_BUDGET.txt 2>execution2_compute_$NRUNS_$RUN_START_FROM_$TIME_BUDGET.txt

        cd $CURRENT_DIRECTORY
        echo $TOOLNAME - Current dir = $PWD
        echo $TOOLNAME - ... Computation finished for $TOOLNAME with time budget = $TIME_BUDGET
}

for TIME_BUDGET in ${@:3} # $3 onwards
do	for TOOLNAME in evosuite t3 jtexpert randoop # contest tools
	do      run_tool_process $TOOLNAME $TIMEBUDGET &
	done
	# wait for all process being completed
	wait
done

echo *** PARALEL SCRIPT FINISHED *** from = $PWD at $(date +%Y/%m/%d-%H:%M:%S)
