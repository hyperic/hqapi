#!/bin/sh
DIR=`dirname $0`

## Create a new role
curl -uhqadmin http://localhost:7080/hqu/hqapi1/role/create.hqu -Fpostdata=@"$DIR/roleSyncUpdate.xml"

curl -uhqadmin http://localhost:7080/hqu/hqapi1/role/sync.hqu -Fpostdata=@"$DIR/roleSyncCreateValid.xml"
