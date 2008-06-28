#!/bin/sh

ID=`date +%s`
NAME=apitest-${ID}

curl -uhqadmin "http://localhost:7080/hqu/hqapi1/user/create.hqu?Name=${NAME}&FirstName=API&LastName=Test&Password=apitest&EmailAddress=apitest%40hyperic.com&Active=true"
