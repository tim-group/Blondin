#!/bin/bash

BUILD_DIR="../build"
THROTTLE_SIZE=15

echo "/slow" > $BUILD_DIR/slowresources.txt

echo "port=8082
targetPort=8081
targetHost=localhost
throttleSize=$THROTTLE_SIZE
expensiveResourcesUrl=file://`pwd`/$BUILD_DIR/slowresources.txt" > $BUILD_DIR/blondin.properties

java -jar jruby-complete*.jar server.rb $THROTTLE_SIZE &
server_pid=$!

java -jar $BUILD_DIR/Blondin-main*.jar $BUILD_DIR/blondin.properties &
blondin_pid=$!

while ! nc -z localhost 8081; do sleep 1; done
while ! nc -z localhost 8082; do sleep 1; done

java -jar jruby-complete*.jar test.rb
test_exit_code=$?

kill $server_pid
kill $blondin_pid

exit $test_exit_code
