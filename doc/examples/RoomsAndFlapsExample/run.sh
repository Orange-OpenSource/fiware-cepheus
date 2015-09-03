#!/bin/sh
# Rooms and Floors example

CEP=localhost:8080

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

echo ""
echo "#2 Then send T° of all the rooms to the CEP every 5 sec"

for temp in 12 14 18 20 24 19; do

	echo ""
	echo "# Wait between temperatures updates..."
	echo ""
	sleep 1

	for room in 1 2 3 4; do
		for floor in 1 2 3; do
			# compute a unique temp for each room
			t=$(($temp + (2*$floor) + $room))
    		echo " - Send updateContext for Room$floor$room with T°=$t"
    		out=$(sendRoomTemp $CEP "Floor$floor" "Room$floor$room" "$t" "Flap$floor$room")
			echo "   $out"
		done
	done

	for room in 1 2 3 4; do
		for floor in 1 2 3; do
			# compute a unique temp for each room
			t=$(($temp + (2*$floor) + $room + 1))
    		echo " - Send updateContext for Room$floor$room with T°=$t"
    		out=$(sendRoomTemp $CEP "Floor$floor" "Room$floor$room" "$t" "Flap$floor$room")
			echo "   $out"
		done
	done
done
