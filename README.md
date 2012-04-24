Blondin
=======

Balance incoming requests to ensure good service by buffering requests for slow/expensive resources.


Blondin is a lightweight HTTP proxy that is capable of throttling requests to a configurable subset of proxied resources.  

Blondin utilises a handful of special HTTP requests to allow remote control:

    GET   /status
    POST  /stop
    POST  /suspend


Blondin is named after [Charles Blondin][blondin-wiki], a famous tightrope walker, who was unarguably good at balancing.

[![][blondin-img]][blondin-wiki]

[blondin-img]: http://upload.wikimedia.org/wikipedia/commons/7/7e/Charles.Blondin.jpg
[blondin-wiki]: http://en.wikipedia.org/wiki/Charles_Blondin
