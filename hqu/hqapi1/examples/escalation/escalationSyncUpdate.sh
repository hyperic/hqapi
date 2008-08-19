#!/bin/sh
DIR=`dirname $0`

## Sync Create Escalation
curl -uhqadmin http://localhost:7080/hqu/hqapi1/escalation/sync.hqu -Fpostdata=@"$DIR/escalationSyncCreateForUdpate.xml"

## Sync Update Escalation
curl -uhqadmin http://localhost:7080/hqu/hqapi1/escalation/sync.hqu -Fpostdata=@"$DIR/escalationSyncUpdate.xml"
