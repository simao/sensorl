package io.simao.sensorl.db

import java.io.File
import java.nio.file.{Paths, Path}
import com.typesafe.scalalogging.LazyLogging
import io.simao.librrd.{RRDTool, LibRRD}
import io.simao.sensorl.message.Measurement
import io.simao.util.KestrelObj._
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.ISODateTimeFormat
import io.simao.util.DateTimeToEpoch._

// TODO: Looks like there is a problem if the file is empty!

class MetricKey(repr: String) {
  type MetricType = String
  
  // TODO: Invalid repr string?

  def file()(implicit basePath: File): File = {
    val filePath = Paths.get(basePath.getAbsolutePath, repr.split("/")(0) + ".rrd")
    new File(filePath.toString)
  }

  def name: String = {
    repr.split("/").lift(1).getOrElse(repr)
  }

  def metricType: MetricType = "GAUGE"
}

object MeasurementDatabase {
  implicit val basePath = new File("db/")
  
  def apply(key: String): MeasurementDatabase = {
    val metric = new MetricKey(key)
    val file = metric.file

    if(!file.exists()) {
      createMetricsFile(metric)
    }
    
    new MeasurementDatabase(file.getAbsolutePath)
  }

  // TODO: Multiple threads trying to create file raises havoc? Maybe not
  private def createMetricsFile(metric: MetricKey): Unit = {
    val rrdArgs = Array(
      s"DS:${metric.name}:${metric.metricType}:20:-1:50",
      "RRA:AVERAGE:0.5:1:8640",
      "RRA:AVERAGE:0.5:12:2400",
      "RRA:MIN:0.5:12:2400",
      "RRA:MAX:0.5:12:2400")

    // MAybe this is the problem?
    val start = (new DateTime).toEpoch.toInt

    RRDTool.create(metric.file.getAbsolutePath, 10, start, rrdArgs)
  }
}

class MeasurementDatabase(fileName: String) extends LazyLogging {
  type MeasurementT = (Long, Double)

  val timeParser = ISODateTimeFormat.dateTimeParser().withZone(DateTimeZone.UTC)

  def save(item: Measurement): Measurement = {
    item.tap { i ⇒
      val date = timeParser.parseDateTime(i.time)
      val unixTime = java.lang.Long.valueOf(date.toEpoch)
      RRDTool.update(fileName, unixTime, i.value);
    }
  }

  def fetchValues(start: DateTime, cf: String = "AVERAGE",
                   step: Long = 10): List[MeasurementT] = {
    val endM = (new DateTime).toEpoch
    val startM = start.toEpoch

    val v = RRDTool.fetch(fileName, cf, startM, endM, step)
    assert(v.getDs_cnt == 1, "Only one data source per file is supported")
    val data = v.getData

    // TODO: Error handling, mutation
    val res = for {
      idx ← Range(0, data.length) // We assume we only have one data source
      ts = v.getStart + idx * v.getStep
    } yield (ts * 1000, data(idx))

    res.toList
  }
}
