package com.example

import java.io.InputStream
import java.security.{KeyStore, SecureRandom}

import akka.http.scaladsl.{HttpsConnectionContext, UseHttp2}
import javax.net.ssl.{KeyManagerFactory, SSLContext}

object Security {
  private def resourceStream(resourceName: String): InputStream = {
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
}
