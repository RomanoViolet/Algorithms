#!/usr/bin/env bash

# Recompile all sources into .class files
for f in $(ls $(pwd)/src/*.java)
do
    pmd $f
    
done
