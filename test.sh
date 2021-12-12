#!/bin/bash

set -ex

URL=${1:-"http://localhost:8080"}

curl -X POST -w "\n" -d 'my first task' -H "Content-Type: text/plain" ${URL}/add
curl -X POST -w "\n" -d 'my second task' -H "Content-Type: text/plain" ${URL}/add
curl -w "\n" -s -X GET ${URL}/get | jq
curl -w "\n" -s -X GET ${URL}/get/1 | jq
curl -X PUT -d 'changed task' -H "Content-Type: text/plain" ${URL}/update/1
curl -w "\n" -s -X GET ${URL}/get/1 | jq
curl -X DELETE ${URL}/delete/1
curl -w "\n" -s -X GET ${URL}/get | jq
