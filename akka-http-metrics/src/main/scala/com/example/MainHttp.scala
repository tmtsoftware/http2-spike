package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.server.HttpApp
import com.typesafe.config.ConfigFactory

object MainHttp extends HttpApp with App {

//  val prometheusRegistry = PrometheusRegistry()

  val routes = allRoutes

  val config = ConfigFactory.load()
  private implicit val actorSystem: ActorSystem =
    ActorSystem("http1", config.getConfig("http1-server-settings"))

  startServer("0.0.0.0", 9001, actorSystem)
}
