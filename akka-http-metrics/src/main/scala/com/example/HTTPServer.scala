package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.util.{Failure, Success}

object HTTPServer {
  val config = ConfigFactory.load()

  private implicit val actorSystem: ActorSystem =
    ActorSystem("http2", config.getConfig("http1-server-settings"))
  import actorSystem._
  private implicit val mat = ActorMaterializer()

  def start(): Future[Http.ServerBinding] = {
    val f =
      Http().bindAndHandleAsync(Route.asyncHandler(allRoutes), "0.0.0.0", 9001)

    f.onComplete {
      case Success(binding) =>
        println(s"server bound to ${binding.localAddress}")
      case Failure(exception) =>
        println(s"error while starting server")
        exception.printStackTrace()
    }
    f
  }
}
