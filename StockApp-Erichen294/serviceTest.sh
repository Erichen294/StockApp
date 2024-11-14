#!/usr/bin/env bash
clear

echo "starting queue service in new process"
gradle queue-service:runService &
queueServerPid=$!
echo "queue server pid is $queueServerPid"

echo "waiting for queue server to come up"
sleep 5

echo "starting mock finnhub service in new process"
gradle finnhub-service:runServiceWithMockFinhubPreload &
finnhubServerPid=$!
echo "Finnhub server pid is $finnhubServerPid"

echo "waiting for finnhub server to come up"
sleep 5

echo "starting monolith-service in new process"
gradle monolith-service:runServer &
monoServerPid=$!
echo "monolith-service server pid is $monoServerPid"

echo "waiting for mono server to come up"
sleep 15

echo "starting server test"
gradle monolith-service:executeServerIntegrationTest
# capture output of integration test
testResult=$?

echo "stopping server"
kill -9 $queueServerPid
kill -9 $finnhubServerPid
kill -9 $monoServerPid
# exit with result of integration test and not the above kill command which would have been the default
exit $testResult