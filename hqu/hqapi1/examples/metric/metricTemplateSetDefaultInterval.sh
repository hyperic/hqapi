#!/bin/sh
## Provide the metric template id
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/metric/setDefaultInterval.hqu?templateId=10001&interval=2"
