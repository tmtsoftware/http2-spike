package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import fr.davit.akka.http.metrics.core.scaladsl.server.HttpMetricsDirectives._
import fr.davit.akka.http.metrics.core.scaladsl.server.HttpMetricsRoute._
import fr.davit.akka.http.metrics.prometheus.PrometheusRegistry
import fr.davit.akka.http.metrics.prometheus.marshalling.PrometheusMarshallers._

import scala.concurrent.Future
import scala.util.{ Failure, Success }

object HTTP_Server {

  val prometheusRegistry = PrometheusRegistry()

  val config = ConfigFactory.load()

  private implicit val actorSystem: ActorSystem =
    ActorSystem("http1", config.getConfig("http1-server-settings"))
  import actorSystem._
  private implicit val mat = ActorMaterializer()

  val routesFlow = (path("metrics") {
    metrics(prometheusRegistry)
  } ~ allRoutes)
    .recordMetrics(prometheusRegistry)

  def start(): Future[Http.ServerBinding] = {
    val f =
      Http().bindAndHandle(routesFlow, "0.0.0.0", 9001)

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
