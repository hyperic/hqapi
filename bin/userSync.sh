#!/bin/sh

cd `dirname $0`
RUNDIR=`pwd`

${RUNDIR}/runCommand.sh org.hyperic.hq.hqapi1.tools.UserSync $@