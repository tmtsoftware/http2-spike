package com.example

import java.io.InputStream
import java.security.{ KeyStore, SecureRandom }

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.{ Http, HttpsConnectionContext, UseHttp2 }
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import javax.net.ssl.{ KeyManagerFactory, SSLContext }

import scala.concurrent.Future
import scala.util.{ Failure, Success }

object HTTPSServer {

  def httpsConnectionContext: HttpsConnectionContext = {
    def resourceStream(resourceName: String): InputStream = {
      val is = getClass.getClassLoader.getResourceAsStream(resourceName)
      require(is ne null, s"Resource $resourceName not found")
      is
    }

    val serverContext: HttpsConnectionContext = {
      // never put passwords into code!
      val password = "abcdef".toCharArray

      val ks = KeyStore.getInstance("PKCS12")
      val stream = resourceStream("keys/server.p12")
      ks.load(stream, password)

      val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
      keyManagerFactory.init(ks, password)

      val context = SSLContext.getInstance("TLS")
      context.init(keyManagerFactory.getKeyManagers, null, new SecureRandom)

      new HttpsConnectionContext(context, http2 = UseHttp2.Always)
    }
    serverContext
  }

  val config = ConfigFactory.load()

  private implicit val actorSystem: ActorSystem =
    ActorSystem("http2", config.getConfig("http2-server-settings"))
  import actorSystem._
  private implicit val mat = ActorMaterializer()

  def start(): Future[Http.ServerBinding] = {
    val f = Http().bindAndHandleAsync(
      Route.asyncHandler(allRoutes),
      "0.0.0.0",
      9000,
      httpsConnectionContext)

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
