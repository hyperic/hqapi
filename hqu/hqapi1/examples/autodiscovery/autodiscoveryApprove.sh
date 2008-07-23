#!/bin/sh

## Approve the platform with the given id
curl -uhqadmin http://localhost:7080/hqu/hqapi1/autodiscovery/approve.hqu?id=10002
