#!/bin/sh
# Rooms and Flaps example

CEP=localhost:8080
LB=localhost:8081
IOT=localhost:8083

. ../common.sh

# Send an updateContext request with Room temp
function sendRoomTemp() #(url, floor, room, value, flap)
{
    payload='{
		"contextElements": [
			{
				"type": "Room",
				"isPattern": "false",
				"id": "'$3'",
				"attributes": [
			   		{
						"name": "temperature",
						"type": "double",
						"value": '$4'
					},
				   	{
						"name": "floor",
						"type": "string",
						"value": "'$2'"
					},
					{
						"name": "flap",
						"type": "string",
						"value": "'$5'"
					}
				]
			}
		],
		"updateAction": "UPDATE"
	}'

	send $1 "v1/updateContext" "$payload"
}

echo "#1 First update CEP with RoomsAndFloors configuration"
CONFIG=`cat config.json`
updateConfig $CEP "$CONFIG"

echo "#2 Send registerContext for Flap$floor$room"
out=$(send $IOT "v1/admin/registerFlap" "")
echo "   $out"

echo ""
echo "#6 Then send T° of all the rooms to the CEP every 5 sec"
out=$(send $IOT "v1/admin/updateRoom" "")
echo "   $out"

