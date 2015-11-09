#!/bin/sh
# Simple script to send updateContext to a NGSI endpoint (supporting v1 with JSON)

. common.sh

if (( "$#" < "8" )); then
	echo "Usage: $0 endpoint sleep entity_type entity_id attr_name attr_type attr_value1 ... attr_valueN"
    echo "Example: Send temp every sec: $0 http://localhost:8080 1 Room Room1 temperature float 10 12 14 16 18 20"
	exit 1
fi

function sendUpdateContext() #(url, entity_type, entity_id, attr_name, type, value)
{
    payload='{
             "contextElements": [
                 {
                     "type": "'$2'",
                     "isPattern": "false",
                     "id": "'$3'",
                     "attributes": [
                     	{
                        	"name": "'$4'",
                        	"type": "'$5'",
                        	"value": "'$6'"
                     	}
                     ]
                 }
             ],
             "updateAction": "APPEND"
         }'

	send $1 "updateContext" "$payload"
}

url=$1; shift
sleep=$1 shift
entity_type=$1; shift
entity_id=$1; shift
attr_name=$1; shift
attr_type=$1; shift

while (( "$#" )); do
    echo "Send updateContext to $1"

    sendUpdateContext $url $entity_type $entity_id $attr_name $attr_type $1
    shift

    sleep $sleep
done
