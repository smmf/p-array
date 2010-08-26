#!/bin/bash

CLASSPATH="classes"

for i in lib/*; do
    CLASSPATH=${CLASSPATH}:${i}
done

java -Xmx512m -cp ${CLASSPATH} Main $*
