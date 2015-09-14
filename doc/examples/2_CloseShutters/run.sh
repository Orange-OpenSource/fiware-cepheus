#!/bin/sh
# CloseShutters example

CEP=localhost:8080

. ../common.sh

# Send an updateContext request with Room temp
function sendRoomTemp() #(url, room, value, shutter)
{
    payload='{
		"contextElements": [
			{
				"type": "Room",
				"isPattern": "false",
				"id": "'$2'",
				"attributes": [
			   		{
						"name": "temperature",
						"type": "double",
						"value": "'$3'"
					},
					{
						"name": "shutter",
						"type": "string",
						"value": "'$4'"
					}
				]
			}
		],
		"updateAction": "UPDATE"
	}'

	send $1 "v1/updateContext" "$payload"
}

echo "#1 First update CEP configuration"
CONFIG=`cat config.json`
updateConfig $CEP "$CONFIG"

echo ""
echo "#2 Then send T° of all the rooms to the CEP every 5 sec"

for temp in 20 20 25 25 25 25 20 20 20 20 20 20 25 25 25 25 25 20 20 20 20 20 20 25 25 25 25 25; do

	echo ""
	echo "# Wait between temperatures updates..."
	echo ""
	sleep 3

	for room in 1 2 3 4; do
			# compute a unique temp for each room
			t=$(($temp + $room))
    		echo " - Send updateContext for Room$room with T°=$t"
    		out=$(sendRoomTemp $CEP "Room$room" "$t" "Shutter$room")
    		# UNCOMMENT TO SEE OUTPUT
			#echo "   $out"
	done
done

#    //"INSERT INTO Shutter SELECT a.shutter as id, 'closed' as status FROM pattern [every a=Room(temperature > cast(25,double)) -> ( (Room(id=a.id, temperature > cast(25,double)) and not Room(id=a.id, temperature <= cast(25,double))) -> (Room(id=a.id, temperature > cast(25,double)) and not Room(id=a.id, temperature <= cast(25,double)))) where timer:within(10 seconds)]"
#    "INSERT INTO Shutter SELECT a.shutter as id, 'closed' as status FROM pattern [every a=Room(temperature > 25.0) -> ((Room(id=a.id, temperature > 25.0) and not Room(id=a.id, temperature <= 25.0)) -> (Room(id=a.id, temperature > 25.0) and not Room(id=a.id, temperature <= 25.0))) where timer:within(10 seconds)]"
# "INSERT INTO RoomAvg select id, shutter, avg(temperature) as temperature FROM Room.win:time(10 sec)",
#    "INSERT INTO Shutter SELECT distinct shutter as id, 'close' as status FROM RoomAvg(temperature > 26.0) as t output every 10 sec",
#    "INSERT INTO Shutter SELECT distinct shutter as id, 'open' as status FROM RoomAvg(temperature < 24.0) as t output every 10 sec"

#"INSERT INTO Shutter SELECT r.shutter as id, 'open' as status FROM pattern [ every r=Room(temperature > 26) -> (timer:interval(10 sec) and not Room(temperature < 24.0 and id=r.id))]",
#    "INSERT INTO Shutter SELECT r.shutter as id, 'close' as status FROM pattern [ every r=Room(temperature < 24) -> (timer:interval(10 sec) and not Room(temperature > 26.0 and id=r.id))]"


