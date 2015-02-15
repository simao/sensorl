package io.simao.sensorl.db

import java.io.File
import com.typesafe.scalalogging.LazyLogging
import io.simao.librrd.LibRRD
import io.simao.sensorl.message.Measurement
import io.simao.util.KestrelObj._
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.ISODateTimeFormat


object MeasurementDatabase {
  def apply(fileName: String) = {
    val file = new File(fileName).getAbsolutePath
    new MeasurementDatabase(file)
  }
}

class MeasurementDatabase(fileName: String) extends LazyLogging {
  val timeParser = ISODateTimeFormat.dateTimeParser().withZone(DateTimeZone.UTC)

  def save(item: Measurement): Measurement = {
    item.tap { i ⇒
      val date = timeParser.parseDateTime(i.time)
      val unixTime = java.lang.Long.valueOf(date.getMillis / 1000l)
      val args = Array(s"N:${i.value}")
      LibRRD.rrdupdate(fileName, args)
    }
  }

  def setupDb(drop: Boolean): Unit = {
    val f = new File(fileName)

    if (drop) {
      if(f.delete())
        logger.info("Database {} deleted", fileName)
    }

    if(!f.exists()) {
      val rrdArgs = Array(
        "DS:temp:GAUGE:20:-1:50",
        "RRA:AVERAGE:0.5:1:8640",
        "RRA:AVERAGE:0.5:12:2400",
        "RRA:MIN:0.5:12:2400",
        "RRA:MAX:0.5:12:2400")

      LibRRD.rrdcreate(fileName, 10, 0, rrdArgs)
    }
  }

  def graph(file: File) = {
    val rrdArgs = Array(
      "dummy", // TODO: This should be on c side
      file.getAbsolutePath,
      "--end", "now",
      "--start", "end-30m",
      "--width=800", "--height=400",
      s"DEF:ds0a=$fileName:temp:AVERAGE", // TODO: temp?!?
      "LINE1:ds0a#0000FF:\"default resolution\""
    )

    LibRRD.rrdgraph(rrdArgs)
  }
}
