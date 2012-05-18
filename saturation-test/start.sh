#!/bin/bash

echo "/slow" > slowresources.txt

echo "port=8082
targetPort=8081
targetHost=localhost
expensiveResourcesUrl=file://`pwd`/slowresources.txt" > blondin.properties

java -jar jruby-complete*.jar server.rb &
server_pid=$!

java -jar ../build/Blondin-main*.jar blondin.properties &
blondin_pid=$!

while ! nc -vz localhost 8081; do sleep 1; done
while ! nc -vz localhost 8082; do sleep 1; done

java -jar jruby-complete*.jar test.rb
test_exit_code=$?

kill $server_pid
kill $blondin_pid

exit $test_exit_code
