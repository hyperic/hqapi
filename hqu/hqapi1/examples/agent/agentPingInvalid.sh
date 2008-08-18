#!/bin/sh 
## agent id doesn't exist; objectNotFound exception should be thrown
curl -uhqadmin http://localhost:7080/hqu/hqapi1/agent/pingAgent.hqu?id=100
