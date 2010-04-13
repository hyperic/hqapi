#!/bin/sh

RUNDIR=`dirname $0`/..

HQAPI_JAR=`ls $RUNDIR/*.jar`
CLASSPATH=$CLASSPATH:$RUNDIR/conf:$HQAPI_JAR

for i in `ls $RUNDIR/lib/*.jar`; do 
   CLASSPATH=$CLASSPATH:$i 
done 

java -cp $CLASSPATH org.hyperic.hq.hqapi1.tools.Shell "$@"
