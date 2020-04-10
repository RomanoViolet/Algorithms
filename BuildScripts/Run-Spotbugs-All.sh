#!/usr/bin/env bash

# Recompile all sources into .class files
for f in $(ls /workspaces/Algorithms/Percolation/bin/*.class)
do
    #echo "Running spotbugs on $f"
    spotbugs $f
done
