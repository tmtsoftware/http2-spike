package com

import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.util.{Timer, TimerTask}

import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.Source

import scala.concurrent.duration.{DurationLong, FiniteDuration}
import scala.concurrent.{Future, Promise}
import scala.language.postfixOps
import scala.util.Try

package object example extends Directives {

  def tickTime: String = ISO_LOCAL_TIME.format(LocalTime.now)

  def wait(duration: FiniteDuration): Future[Unit] = {
    val promise = Promise[Unit]()
    val t = new Timer()
    t.schedule(new TimerTask {
      override def run(): Unit = {
        promise.complete(Try(()))
      }
    }, duration.toMillis)
    promise.future
  }

  import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

  val allRoutes: Route = cors() {
    get {
      path("stream") {
        parameter('durationInSeconds.as[Int]) { durationInSeconds =>
          complete(
            Source
              .tick(0.seconds, 1.second, 0)
              .statefulMapConcat(() => {
                var number: Int = 0
                _ =>
                  {
                    number = number + 1
                    List(number)
                  }
              })
              .takeWithin(durationInSeconds.seconds)
              .map(i => ServerSentEvent(i.toString))
          )
        }
      } ~ (path("entity") & parameter('delay.as[Int] ?)) { delay =>
        //DEFAULTS
        val defaultDelay = 5

        onComplete(wait(delay.getOrElse(defaultDelay).seconds)) { _ =>
          complete("OK")
        }
      }
    }
  }
}
