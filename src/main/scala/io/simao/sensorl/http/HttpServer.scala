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

  def values(start: DateTime): List[(Long, Double)] = {
    db().fetchValues(start, "temp")
  }

  def serializedValues(start: DateTime): String = {
    "[\n" + values(start).map {
        case (ts, v) if v.isNaN ⇒ s"[$ts, null]\n"
        case (ts, v) ⇒ s"[$ts, $v]\n"
    }.mkString(",") + "\n]"
  }

  def parseSince(s: Option[String]): DateTime = {
    val i = s.map(Integer.valueOf)
    new DateTime().minusHours(i.getOrElse[Integer](1))
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val since = parseSince(Option(req.getParameter("since")))

    resp.setContentType("application/json")
    resp.setStatus(HttpServletResponse.SC_OK)
    resp.getWriter.println(serializedValues(since))
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
