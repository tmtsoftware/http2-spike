package com

import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.util.{Timer, TimerTask}

import akka.actor.Cancellable
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.{Done, NotUsed}
import com.example.parameter

import scala.concurrent.duration.{DurationLong, FiniteDuration}
import scala.concurrent.{Future, Promise}
import scala.language.postfixOps
import scala.util.{Random, Try}

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

  def printSink[T]: Sink[T, Future[Done]] = Sink.foreach(println)

  def randomNumberSource(
    duration: Option[FiniteDuration]
  ): Source[TextMessage.Strict, Cancellable] = {
    val source = Source
      .tick(0.second, 500.millis, 0)
      .map(_ => Random.nextInt(100))
      .map(number => TextMessage(number.toString))

    duration match {
      case Some(value) => source.takeWithin(value)
      case None        => source
    }
  }

  val allRoutes: Route = cors() {
    get {
      (path("websocket") & parameter('durationInSeconds.as[Int] ?)) {
        durationInSeconds =>
          handleWebSocketMessages(
            Flow.fromSinkAndSourceCoupled(
              printSink,
              randomNumberSource(durationInSeconds.map(x => x.seconds))
            )
          )
      } ~ path("sse") {
        parameter('durationInSeconds.as[Int] ?) { durationInSeconds =>
          complete {
            val s = Source
              .tick(0.seconds, 500.millis, 0)
              .map(_ => Random.nextInt(100))
            durationInSeconds match {
              case Some(value) =>
                s.takeWithin(value.seconds)
                  .map(i => ServerSentEvent(i.toString))
              case None =>
                s.map(i => ServerSentEvent(i.toString))
            }
          }
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
