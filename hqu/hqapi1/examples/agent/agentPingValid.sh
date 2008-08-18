#!/bin/sh
## Ping agent for the given id; put the correct id for the 'id' parameter below 
curl -uhqadmin http://localhost:7080/hqu/hqapi1/agent/pingAgent.hqu?id=10001
