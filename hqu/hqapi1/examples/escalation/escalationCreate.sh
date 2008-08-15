#!/bin/sh
DIR=`dirname $0`
curl -uhqadmin http://localhost:7080/hqu/hqapi1/escalation/create.hqu -Fpostdata=@"$DIR/escalationCreate.xml"
