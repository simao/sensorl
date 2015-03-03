package io.simao.sensorl.http

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import io.simao.sensorl.db.MeasurementDatabase
import org.joda.time.DateTime

class MetricsApiServlet extends HttpServlet {
  def db(key: String): MeasurementDatabase = {
    MeasurementDatabase(key)
  }

  // TODO: Serialize this in it's own class
  def serializedValues(key: String, start: DateTime): String = {
    val values = db(key).fetchValues(start)

    "[\n" + values.map {
      case (ts, v) if v.isNaN ⇒ s"[$ts, null]\n"
      case (ts, v) ⇒ s"[$ts, $v]\n"
    }.mkString(",") + "\n]"
  }

  def parseSince(s: Option[String]): DateTime = {
    val i = s.map(Integer.valueOf)
    new DateTime().minusHours(i.getOrElse[Integer](1))
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val key = req.getParameter("q")
    val since = parseSince(Option(req.getParameter("since")))
    val serialized = serializedValues(key, since)

    // TODO: 400 if key is not valid/present

    resp.setContentType("application/json")
    resp.setStatus(HttpServletResponse.SC_OK)
    resp.getWriter.println(serialized)
  }
}
