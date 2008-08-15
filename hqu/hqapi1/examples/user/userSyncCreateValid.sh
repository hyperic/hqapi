#!/bin/sh
DIR=`dirname $0`
curl -uhqadmin http://localhost:7080/hqu/hqapi1/user/sync.hqu -F postdata=@"$DIR/userSyncCreateValid.xml"
