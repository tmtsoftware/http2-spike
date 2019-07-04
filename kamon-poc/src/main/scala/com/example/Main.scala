package com.example

import akka.http.scaladsl.server.{HttpApp, Route}
import kamon.Kamon
import kamon.apm.KamonApm
import kamon.prometheus.PrometheusReporter

object Main extends HttpApp with App {

  Kamon.addReporter(new PrometheusReporter())
  Kamon.addReporter(new KamonApm())

  val routes: Route = allRoutes

  startServer("0.0.0.0", 9000)
}
