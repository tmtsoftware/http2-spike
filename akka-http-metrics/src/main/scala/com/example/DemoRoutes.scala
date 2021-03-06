package com

import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.util.{Timer, TimerTask}

import akka.Done
import akka.actor.Cancellable
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.duration.{DurationLong, FiniteDuration}
import scala.concurrent.{Future, Promise}
import scala.language.postfixOps
import scala.util.{Random, Try}

trait DemoRoutes extends Directives {

  def tickTime: String = ISO_LOCAL_TIME.format(LocalTime.now)

  val interval = 2.seconds

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
      .tick(0.second, interval, 0)
      .map(_ => Random.nextInt(100))
      .map(number => TextMessage(number.toString))

    duration match {
      case Some(value) => source.takeWithin(value)
      case None        => source
    }
  }

  val allRoutes: Route = cors() {
    get {
      path("random") {
        complete(Random.nextInt(100).toString)
      } ~ (path("websocket") & parameter('durationInSeconds.as[Int] ?)) {
        durationInSeconds =>
          optionalHeaderValueByName('someHeader) { someHeader =>
            if (durationInSeconds.getOrElse(0) >= 0)
              handleWebSocketMessages(
                Flow.fromSinkAndSource(
                  printSink,
                  randomNumberSource(durationInSeconds.map(x => x.seconds))
                )
              )
            else {
              complete(
                StatusCodes.BadRequest -> s"durationInSeconds should be more than 0. value '${durationInSeconds}' is invalid"
              )
            }
          }
      } ~ path("sse") {
        parameter('durationInSeconds.as[Int] ?) { durationInSeconds =>
          respondWithHeaders(
            RawHeader("X-Accel-Buffering", "no"),
            RawHeader("Cache-Control", "no-cache"),
          ) {
            complete {
              val s = Source
                .tick(0.seconds, interval, 0)
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
        }
      }
    }
  }
}
