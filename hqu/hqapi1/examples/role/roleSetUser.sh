#!/bin/sh

## Create test role
DIR=`dirname $0`
curl -uhqadmin http://localhost:7080/hqu/hqapi1/role/create.hqu -Fpostdata=@"$DIR/roleCreateForSetUser.xml"

## Create test user
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/user/create.hqu?name=roleTestuser&firstName=API&lastName=Test&password=apitest&emailAddress=apitest%40hyperic.com&Active=true"

## Set hqadmin user to the test role
curl -uhqadmin http://localhost:7080/hqu/hqapi1/role/setUsers.hqu -Fpostdata=@"$DIR/roleSetUser.xml"