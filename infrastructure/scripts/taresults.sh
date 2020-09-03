#!/bin/bash

# author: Urko Rueda (2016)

find . -type f \( -name \*\transcript\.csv \) | xargs tar -zcvf transcripts.tar.gz
