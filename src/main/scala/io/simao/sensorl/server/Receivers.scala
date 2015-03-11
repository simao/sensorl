package io.simao.sensorl.server

import com.typesafe.scalalogging.LazyLogging
import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.message.Measurement

trait Receiver {
  def receive(m: Measurement): Unit
}

class LoggingReceiver extends Receiver with LazyLogging {
  override def receive(m: Measurement): Unit = {
    logger.info(m.toJson())
  }
}

class DatabaseReceiver extends Receiver with LazyLogging {
  def receive(m: Measurement): Unit = {
    logger.info("new measurement" + m.toJson())
    MeasurementDatabase(m.key).save(m)
  }
}
