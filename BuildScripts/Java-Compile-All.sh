#!/usr/bin/env bash

# Recompile all sources into .class files
for f in $(ls /workspaces/Algorithms/Percolation/src/*.java)
do
    echo "Compiling $f"
    javac $f \
    --class-path "/usr/local/algs4/algs4.jar" \
    --source-path "/workspaces/Algorithms/Percolation/src" \
    -d "/workspaces/Algorithms/Percolation/bin"
done
