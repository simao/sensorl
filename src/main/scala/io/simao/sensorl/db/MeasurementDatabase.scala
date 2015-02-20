package io.simao.sensorl.db

import java.io.File
import com.typesafe.scalalogging.LazyLogging
import io.simao.librrd.{RRDTool, LibRRD}
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
      RRDTool.update(fileName, unixTime, i.value);
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

      RRDTool.create(fileName, 10, 0, rrdArgs)
    }
  }
}
