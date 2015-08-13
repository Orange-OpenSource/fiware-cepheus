#!/bin/bash
# Send config.json to CEP

. common.sh

if (( "$#" < "1" )); then
    echo "Usage: $0 endpoint"
    echo "Example: $0 http://localhost:8080"
fi

CONFIG=`cat config.json`

updateConfig "$1" "$CONFIG"
