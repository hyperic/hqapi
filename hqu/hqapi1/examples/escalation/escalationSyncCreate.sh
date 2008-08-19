#!/bin/sh
DIR=`dirname $0`
curl -uhqadmin http://localhost:7080/hqu/hqapi1/escalation/sync.hqu -Fpostdata=@"$DIR/escalationSyncCreate.xml"
