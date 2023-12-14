#!/bin/bash

# authors: Annibale Panichella and Urko Rueda (2017)

if [ $# -lt 2 ]
then
        echo "TranscriptPath and OutputFolder expected as arguments"
        echo "example: this_script myfolder/my_single_transcript.csv score_output_folder"
        exit 0;
fi

RSCRIPT=/usr/local/bin/statistical_analysis.R
TRANSCRIPT=$1 # path to single transcript
OUTPUT=$2 # directory where to save the results

echo "Runing R scoring script: Rscript $RSCRIPT $TRANSCRIPT $OUTPUT"
Rscript $RSCRIPT $TRANSCRIPT $OUTPUT
