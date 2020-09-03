#!/bin/bash

# run the script at contest results folders
echo "cleaning metrics folders ..."
find  -type d -name "metrics" -exec rm -rf {} \;
echo "  ... done"
