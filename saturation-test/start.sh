#!/bin/bash

java -jar jruby-complete*.jar server.rb &
server_pid=$!

java -jar ../build/Blondin-main*.jar blondin.properties &
blondin_pid=$!

java -jar jruby-complete*.jar test.rb
test_exit_code=$?

kill $server_pid
kill $blondin_pid

exit $test_exit_code
