#!/bin/sh

## Replace the address value with the intended address
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/agent/getAgent.hqu?address=x.x.x.x&port=2144"
