package io.simao.sensorl.http

import java.io.File
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import com.typesafe.scalalogging.LazyLogging
import io.simao.sensorl.db.MeasurementDatabase

import org.eclipse.jetty.server.{Handler, Server}
import org.eclipse.jetty.server.handler.{DefaultHandler, HandlerList, ResourceHandler}
import org.eclipse.jetty.servlet.ServletHandler
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}

class MetricsApiServlet extends HttpServlet {

  // TODO: How to inject this dependency
  def db(): MeasurementDatabase = {
    MeasurementDatabase("temp.rrd")
  }

  def values(): List[(Long, Double)] = {
    db().fetchValues(new DateTime(), "temp")
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    resp.setContentType("text/html")
    resp.setStatus(HttpServletResponse.SC_OK)
    resp.getWriter().println("<h1>Hello from HelloServlet</h1>")
    resp.getWriter().println(values().mkString("<br>\n"))
  }
}

class HttpServer(val dir: File, val port: Int = 8080) extends LazyLogging {
  def start()(implicit ec: ExecutionContext) = {
    val server = new Server(port)
    val resource_handler = new ResourceHandler()

    resource_handler.setDirectoriesListed(true)
    resource_handler.setWelcomeFiles(Array[String]("index.html"))
    resource_handler.setResourceBase(dir.getAbsolutePath)

    val handler = new ServletHandler()
    handler.addServletWithMapping(classOf[MetricsApiServlet], "/api/*")

    val handlers = new HandlerList()
    handlers.setHandlers(Array[Handler](resource_handler, handler, new DefaultHandler()))

    server.setHandler(handlers)

    logger.info("Starting http server on port {} serving {}", port.toString, dir.toString)

    Future {
      server.start()
      server.join()
    }
  }
}
