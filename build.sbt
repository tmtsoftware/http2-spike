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
  .aggregate(akkaHTTPMetrics)

lazy val akkaHTTPMetrics = (project in file("./akka-http-metrics"))
  .settings(
    name := "AkkaHttpHttp2POC",
    libraryDependencies ++= Seq(
      "fr.davit" %% "akka-http-metrics-prometheus" % "0.4.0"
    ),
  )

lazy val httpClient = (project in file("./http-client"))
  .settings(
    name := "httpClient",
    libraryDependencies ++= Seq(
      "com.github.alexarchambault" % "case-app_2.12" % "2.0.0-M9"
    )
  )
