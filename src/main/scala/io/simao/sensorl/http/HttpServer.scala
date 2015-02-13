package io.simao.sensorl.http

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jetty.server.{Handler, Server}
import org.eclipse.jetty.server.handler.{DefaultHandler, HandlerList, ResourceHandler}

import scala.concurrent.{ExecutionContext, Future}


class HttpServer(val dir: File, val port: Int = 8080) extends LazyLogging {
  def start()(implicit ec: ExecutionContext) = {
    val server = new Server(port)
    val resource_handler = new ResourceHandler()

    resource_handler.setDirectoriesListed(true)
    resource_handler.setWelcomeFiles(Array[String]("index.html"))
    resource_handler.setResourceBase(dir.getAbsolutePath)

    val handlers = new HandlerList()
    handlers.setHandlers(Array[Handler](resource_handler, new DefaultHandler()))
    server.setHandler(handlers)

    logger.info("Starting http server on port {} serving {}", port.toString, dir.toString)

    Future {
      server.start()
      server.join()
    }
  }
}
