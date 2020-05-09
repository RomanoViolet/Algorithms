#!/usr/bin/env bash

# Recompile all sources into .class files

echo "PWD: " $(pwd)
for f in $(ls $(pwd)/src/*.java)
do
    echo "Compiling $f"
    javac $f \
    --class-path "/usr/local/algs4/algs4.jar" \
    --source-path "$(pwd)/src" \
    -d "$(pwd)/bin"
done
