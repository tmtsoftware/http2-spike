package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.{ HttpMethods, HttpRequest, Uri }
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import akka.{ Done, NotUsed }
import caseapp.core.RemainingArgs
import caseapp.core.app.CaseApp

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong
import scala.util.{ Failure, Success }

object Main extends CaseApp[Options] {

  override def run(options: Options, remainingArgs: RemainingArgs): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem()
    import actorSystem._
    implicit val mat: ActorMaterializer = ActorMaterializer()

    val startTime = System.currentTimeMillis()

    val targets = Seq("192.168.0.102", "10.211.55.2", "10.37.129.2")

    var currentIndex = 0

    var ipCounter = 0

    def nextIp: String = {
      ipCounter = ipCounter + 1
      currentIndex match {
        case x if x == 2 =>
          currentIndex = 0
          targets(x)
        case x =>
          currentIndex = currentIndex + 1
          targets(x)
      }
    }

    def sseRequest(i: Int): Future[Done] = {
      val ip = nextIp

      val requestF = Http()
        .singleRequest(
          HttpRequest(
            HttpMethods.GET,
            Uri(
              s"http://$ip:9000/stream?durationInSeconds=${options.durationInSeconds}")))

      requestF.onComplete {
        case Failure(exception) =>
          printWithTime(s"stream $i failed to start")
          exception.printStackTrace()
        case Success(_) =>
        //          printWithTime(s"stream $i started")
      }

      val streamF = requestF
        .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
        .flatMap(s => s.runWith(Sink.ignore))
      //        .flatMap(x => {
      //          x.runForeach(x => {
      //            printWithTime(s"($i) ${x.data} ($ip)")
      //          })
      //        })

      streamF.onComplete {
        case Failure(exception) =>
          printWithTime(s"stream $i failed with error")
          exception.printStackTrace()
        case Success(_) =>
          printWithTime(s"stream $i completed")
      }

      streamF
    }

    def entityRequest(i: Int): Future[Done] = {
      Http()
        .singleRequest(
          HttpRequest(HttpMethods.GET, Uri(s"http://$nextIp:9000/entity")))
        .map(s => {
          printWithTime(s"($i) ${s.status.value}")
          Done
        })
    }

    def printWithTime(value: String): Unit = {
      val currentTime = System.currentTimeMillis()
      println(
        s"${(currentTime - startTime).millis.toSeconds.formatted("%02d")}s : $value")
    }

    val spawn: Future[List[Done]] = Future
      .sequence((1 to options.parallelRequests).toList.map(sseRequest))

    printWithTime(s"$ipCounter ips created")
    printWithTime(s"spawned ${options.parallelRequests} requests")

    spawn
      .onComplete {
        case Success(_) =>
          printWithTime(s"SUCCESS")
          actorSystem.terminate()
        case Failure(e) =>
          e.printStackTrace()
          actorSystem.terminate()
      }
  }
}
