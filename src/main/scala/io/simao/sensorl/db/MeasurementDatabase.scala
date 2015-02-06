package io.simao.sensorl.db

import java.io.File
import com.typesafe.scalalogging.LazyLogging
import io.simao.librrd.LibRRD
import io.simao.sensorl.message.Measurement
import io.simao.util.KestrelObj._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat


object MeasurementDatabase {
  def apply(fileName: String) = {
    val file = new File(fileName).getAbsolutePath
    new MeasurementDatabase(file)
  }
}

class MeasurementDatabase(fileName: String) extends LazyLogging {
  def save(item: Measurement): Measurement = {
    item.tap { i ⇒
      val date = ISODateTimeFormat.dateTimeParser().parseDateTime(item.time)
      val unixTime = java.lang.Long.valueOf(date.getMillis / 1000l)
      val args = Array(s"N:${item.value}")
      LibRRD.rrdupdate(fileName, args)
    }
  }

  def setupDb(drop: Boolean): Unit = {
    if (drop) {
      if(new File(fileName).delete())
        logger.info("Database {} deleted", fileName)
    }

    val rrdArgs = Array(
      "DS:temp:GAUGE:20:-1:50",
      "RRA:AVERAGE:0.5:1:8640",
      "RRA:AVERAGE:0.5:12:2400",
      "RRA:MIN:0.5:12:2400",
      "RRA:MAX:0.5:12:2400")

    LibRRD.rrdcreate(fileName, 10, 0, rrdArgs)
  }
}
