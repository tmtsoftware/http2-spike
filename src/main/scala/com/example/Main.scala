package com.example

import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME

import akka.NotUsed
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.scaladsl.Source

import scala.concurrent.duration.DurationLong

object Main extends HttpApp with App {

  def tickTime: String = ISO_LOCAL_TIME.format(LocalTime.now)

  val routes: Route = get {
    complete(
      Source
        .tick(1.seconds, 3.seconds, NotUsed)
        .map(_ => ServerSentEvent(tickTime))
    )
  }

  startServer("localhost", 9000)
}
