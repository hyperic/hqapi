#!/bin/sh

DIR=`dirname $0`
curl -uhqadmin http://localhost:7080/hqu/hqapi1/user/update.hqu -Fpostdata=@"$DIR/userUpdate.xml"
