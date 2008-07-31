#!/bin/sh

## Create test role
DIR=`dirname $0`
curl -uhqadmin http://localhost:7080/hqu/hqapi1/role/create.hqu -Fpostdata=@"$DIR/roleCreateForSetUsers.xml"

## Create test users
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/user/create.hqu?name=roleTestuser1&firstName=API&lastName=Test&password=apitest&emailAddress=apitest%40hyperic.com&Active=true"
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/user/create.hqu?name=roleTestuser2&firstName=API&lastName=Test&password=apitest&emailAddress=apitest%40hyperic.com&Active=true"
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/user/create.hqu?name=roleTestuser3&firstName=API&lastName=Test&password=apitest&emailAddress=apitest%40hyperic.com&Active=true"

## Set hqadmin user to the test role
curl -uhqadmin http://localhost:7080/hqu/hqapi1/role/setUsers.hqu -Fpostdata=@"$DIR/roleSetUsers.xml"