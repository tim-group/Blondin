#!/bin/bash

BUILD_DIR="../build"

echo "/slow" > $BUILD_DIR/slowresources.txt

echo "port=8082
targetPort=8081
targetHost=localhost
expensiveResourcesUrl=file://`pwd`/$BUILD_DIR/slowresources.txt" > $BUILD_DIR/blondin.properties

java -jar jruby-complete*.jar server.rb &
server_pid=$!

java -jar $BUILD_DIR/Blondin-main*.jar $BUILD_DIR/blondin.properties &
blondin_pid=$!

while ! nc -vz localhost 8081; do sleep 1; done
while ! nc -vz localhost 8082; do sleep 1; done

java -jar jruby-complete*.jar test.rb
test_exit_code=$?

kill $server_pid
kill $blondin_pid

exit $test_exit_code
