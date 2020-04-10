#!/usr/bin/env bash

# Recompile all sources into .class files
for f in $(ls /workspaces/Algorithms/Percolation/src/*.java)
do
    checkstyle -coursera $f
done
