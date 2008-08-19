#!/bin/sh
DIR=`dirname $0`
## Create Escalation
curl -uhqadmin http://localhost:7080/hqu/hqapi1/escalation/create.hqu -Fpostdata=@"$DIR/escalationCreateUpdate.xml"

## Update Escalation
curl -uhqadmin http://localhost:7080/hqu/hqapi1/escalation/update.hqu -Fpostdata=@"$DIR/escalationUpdate.xml"
