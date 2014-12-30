#!/bin/bash

CLASSPATH="classes"

for i in lib/*; do
    CLASSPATH=${CLASSPATH}:${i}
done

# java -Xmx1024m -agentlib:yjpagent -cp ${CLASSPATH} Main $*
java -Xmx1024m -cp ${CLASSPATH} Main $*
