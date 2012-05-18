#!/bin/bash

java -jar jruby-complete*.jar server.rb &
server_pid=$!

java -jar ../build/Blondin-main*.jar blondin.properties &
blondin_pid=$!

java -jar jruby-complete*.jar test.rb

kill $server_pid
kill $blondin_pid
