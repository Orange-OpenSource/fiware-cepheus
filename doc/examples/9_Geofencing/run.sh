#!/bin/sh
# Geofencing example

CEP=localhost:8080

. ../common.sh

function sendTrackerLocation() #(url, id, x)
{
    now_iso8601=`date +"%Y-%m-%dT%H:%M:%SZ"`
    payload='{
		"contextElements": [
			{
				"type": "Tracker",
				"isPattern": "false",
				"id": "'$2'",
				"attributes": [
				    { "name":"time", "type":"date", "value":"'$now_iso8601'" },
			   		{ "name": "location", "type": "geo:point", "value": "'$3', 25" }
				]
			}
		],
		"updateAction": "APPEND"
	}'

	send $1 "v1/updateContext" "$payload"
}

echo "#1 First update CEP with configuration"
CONFIG=`cat config.json`
updateConfig $CEP "$CONFIG"

echo ""
echo "#2 Then send Location of all the trackers to the CEP every 5 sec"

for x in -10 -5 -1 0 1 5 10 25 50 60 100 150; do

	echo ""
	echo "# Wait between location updates..."
	echo ""
	sleep 5

	for tracker in 1; do
    	echo " - Send updateContext for Tracker$tracker with X=$x"
    	out=$(sendTrackerLocation $CEP "Tracker$tracker" "$x")
		echo "   $out"
	done
done
