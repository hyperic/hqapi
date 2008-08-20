#!/bin/sh
## Provide the metric template id
curl -uhqadmin "http://localhost:7080/hqu/hqapi1/metric/setDefaultOn.hqu?templateId=10001&on=false"
