#!/bin/bash

cd `dirname $0`/..
RUNDIR=`pwd`

CLASSPATH=$CLASSPATH$(find $RUNDIR -name "*.jar" -exec printf :{} ';')

java -cp $CLASSPATH $@

