Blondin
=======

Balance incoming requests to ensure good service by buffering requests for slow/expensive resources.


Blondin is a lightweight HTTP proxy that is capable of throttling requests to a configurable subset of proxied resources.  

Blondin takes the form of a single executable jar, and may be started with the following command line:

    java -jar blondin-main-0.0.1.jar myconfig.properties

Configuration is achieved with the use of a properties file:

    # Blondin Core Configuration
    port=9999
    targetHost=localhost
    targetPort=8080
    throttleSize=16
    expensiveResourcesUrl=http://some/expensive/resources/list

    # Logging
    logDirectory=/var/log/blondin

    # Graphite Metrics
    graphite.host=metrics
    graphite.port=2013
    graphite.period=1
    graphite.periodunit=MINUTES

    # StatsD Metrics
    statsd.host=metrics
    statsd.port=2013


Blondin utilises a handful of special HTTP requests to allow remote control:

    GET   /status
    POST  /stop
    POST  /suspend

Colophon
========
Blondin is named after [Charles Blondin][blondin-wiki], a famous tightrope walker, who was unarguably good at balancing.

[![][blondin-img]][blondin-wiki]![][blondinhomage-img]

[blondin-img]: http://upload.wikimedia.org/wikipedia/commons/7/7e/Charles.Blondin.jpg
[blondinhomage-img]: https://raw.github.com/youdevise/Blondin/master/blondinhomage.jpg
[blondin-wiki]: http://en.wikipedia.org/wiki/Charles_Blondin
