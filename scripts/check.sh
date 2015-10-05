#!/bin/bash
# Check the status of Cepheus-CEP and Cepheus-Broker

# Require the IP env variable
if [ -z "$IP" ]; then
    echo "Please define the IP env variable"
    exit 1
fi

# Check and echo all commands
set -e
set -x

# Check services are running
#ssh ubuntu@$IP service cepheus-cep status |grep -q "Cepheus-CEP is running"
#ssh ubuntu@$IP service cepheus-broker status |grep -q "Cepheus-Broker is running"
ssh ubuntu@$IP ps -efd |grep -q "cepheus-cep.jar"
ssh ubuntu@$IP ps -efd |grep -q "cepheus-broker.jar"

# Check services are up
curl $IP:8080/v1/admin/config  -s -S --header "Content-Type: application/json" --header "Accept: application/json"
curl $IP:8081/v1/updateContext -s -S --header "Content-Type: application/json" --header "Accept: application/json" -d @- <<EOF
{
    "contextElements": [
        {
            "type": "Room",
            "isPattern": "false",
            "id": "Room1",
            "attributes": [
            {
                "name": "temperature",
                "type": "float",
                "value": "26.5"
            },
            {
                "name": "pressure",
                "type": "integer",
                "value": "763"
            }
            ]
        }
    ],
    "updateAction": "UPDATE"
}
EOF
