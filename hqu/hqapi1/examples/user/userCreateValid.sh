#!/bin/sh

ID=`date +%s`
NAME=apitest-${ID}

curl -uhqadmin "http://localhost:7080/hqu/hqapi1/user/create.hqu?name=${NAME}&firstName=API&lastName=Test&password=apitest&emailAddress=apitest%40hyperic.com&Active=true"
