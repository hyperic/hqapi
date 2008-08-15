#!/bin/sh
DIR=`dirname $0`

## Create test user
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/user/create.hqu?name=syncUpdateuser&firstName=API&lastName=Test&password=apitest&emailAddress=apitest%40hyperic.com&Active=true"

## Sync update user
curl -uhqadmin http://localhost:7080/hqu/hqapi1/user/sync.hqu -F postdata=@"$DIR/userSyncUpdateValid.xml"