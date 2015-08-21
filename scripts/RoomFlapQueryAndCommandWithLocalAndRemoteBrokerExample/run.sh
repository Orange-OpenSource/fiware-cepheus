#!/bin/sh
# Windows Flaps commands example

IOT=localhost:8083
ORION=localhost:8082

. ../common.sh

# Send an queryContext request
function sendQuery() #(url, name, type, isPattern of entityId)
{
    payload='{
		"name": "'$2'",
		"type": "'$3'",
		"isPattern": "'$4'"
	}'

	send $1 "v1/admin/query" "$payload"
}

function sendUpdate() #(url, name, type, isPattern of entityId and name, type, value of attributs)
{
    payload='{
		"name": "'$2'",
		"type": "'$3'",
		"isPattern": "'$4'",
		"attributName": "'$5'",
		"attributType": "'$6'",
		"attributValue": "'$7'"
	}'

	send $1 "v1/admin/update" "$payload"
}

echo " - Send registerContext for Flap$floor$room"
out=$(send $IOT "v1/admin/registerFlap" "")
echo "   $out"

echo " - Send registerContext for Room$floor$room"
out=$(send $IOT "v1/admin/registerRoom" "")
echo "   $out"

echo ""
echo "# Wait between flap update and query temperature..."
echo ""
sleep 5

echo " - Send queryContext for all Room"
out=$(sendQuery $ORION "Room*" "Room" "true")
echo "   $out"

sleep 1

echo " - Send queryContext for flap21"
out=$(sendQuery $ORION  "Flap21" "Flap" "false")
echo "   $out"

sleep 1
echo " - Send updateContext for flap21"
out=$(sendUpdate $ORION  "Flap21" "Flap" "false" "status" "string" "closed")
echo "   $out"

