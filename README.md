#HTTP 1 vs HTTP 2

This is demo dashboard which fires up 54 SSE 
streams simultaneously to a given server.

The purpose of this dashboard is to demonstrate and compare
HTTP 2 connection optimisations.

> With the new binary framing mechanism in place, HTTP/2 no longer needs multiple TCP connections to multiplex streams in parallel; each stream is split into many frames, which can be interleaved and prioritized. As a result, all HTTP/2 connections are persistent, and only one connection per origin is required, which offers numerous performance benefits.
- Source: https://developers.google.com/web/fundamentals/performance/http2/ 

#Pre-requisites

This UI requires akka-http-metrics(AkkaHttpHttp2POC) server started

you can start the project by running following command:

```bash
sbt "akkaHTTPMetrics/run"
``` 

#Instructions

```bash
cd perf-client
npm install
npm start
```

this will start the webpack server on port 3000. Once started, you can browse
http://localhost:3000/

