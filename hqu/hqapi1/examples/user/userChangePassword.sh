#!/bin/sh

## Create test users
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/user/create.hqu?name=changePasswordTest&firstName=API&lastName=Test&password=apitest&emailAddress=apitest%40hyperic.com&Active=true"

## Change the password
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/user/changePassword.hqu?name=changePasswordTest&password=apitest1"
