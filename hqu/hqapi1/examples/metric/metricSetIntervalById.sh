#!/bin/sh
## Provide metric id & interval
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/metric/setInterval.hqu?id=10001&interval=1"
