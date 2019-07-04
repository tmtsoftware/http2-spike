#!/usr/bin/env bash

mkdir -p buildDir
sbt ";akkaHTTPMetrics/assembly;httpClient/assembly" && \
cp akka-http-metrics/target/scala-2.12/AkkaHttpHttp2POC-assembly-0.1.0-SNAPSHOT.jar ./buildDir/ && \
cp http-client/target/scala-2.12/httpClient-assembly-0.1.0-SNAPSHOT.jar ./buildDir/ && \
echo "DONE"