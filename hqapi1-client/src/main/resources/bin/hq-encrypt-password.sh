#!/bin/sh

RUNDIR=`dirname $0`/..
HQAPILOGDIR=$RUNDIR/logs

CLASSPATH=$CLASSPATH:$RUNDIR/conf

for h in `ls $RUNDIR/*.jar`; do
   CLASSPATH=$CLASSPATH:$h
done

for i in `ls $RUNDIR/lib/*.jar`; do 
   CLASSPATH=$CLASSPATH:$i 
done 

java -Dhqapi.logDir=$HQAPILOGDIR -cp $CLASSPATH org.hyperic.hq.hqapi1.tools.PasswordEncryptor "$@"
