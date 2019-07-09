lazy val akkaHttpVersion = "10.1.8"
lazy val akkaVersion = "2.5.23"

inThisBuild(
  Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.megard" %% "akka-http-cors" % "0.4.1",
      "com.typesafe.akka" %% "akka-http2-support" % "10.1.8",
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    ),
    organization := "com.example",
    scalaVersion := "2.12.7"
  )
)

lazy val root = (project in file("."))
  .settings(name := "POC")
  .aggregate(akkaHTTPMetrics, kamonPOC)

lazy val routes = (project in file("./routes"))
  .settings(name := "routes")

lazy val akkaHTTPMetrics = (project in file("./akka-http-metrics"))
  .settings(
    name := "AkkaHttpHttp2POC",
    libraryDependencies ++= Seq(
      "fr.davit" %% "akka-http-metrics-prometheus" % "0.4.0"
    ),
  )
  .dependsOn(routes)

lazy val kamonPOC = (project in file("./kamon-poc"))
  .settings(
    name := "kamonoPOC",
    libraryDependencies ++= Seq(
      "io.kamon" %% "kamon-core" % "1.1.3",
      "io.kamon" %% "kamon-prometheus" % "1.1.1",
      "io.kamon" %% "kamon-akka-http-2.5" % "1.1.2",
      "io.kamon" %% "kamon-apm-reporter" % "1.1.3"
    )
  )
  .dependsOn(routes)

lazy val httpClient = (project in file("./http-client"))
  .settings(
    name := "httpClient",
    libraryDependencies ++= Seq(
      "com.github.alexarchambault" % "case-app_2.12" % "2.0.0-M9"
    )
  )
