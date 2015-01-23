package io.simao.sensorl

import com.typesafe.scalalogging.LazyLogging
import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.message.Measurement
import io.simao.sensorl.server.Server


trait Receiver {
  def receive(m: Measurement): Unit
}

class LoggingReceiver extends Receiver with LazyLogging {
  override def receive(m: Measurement): Unit = {
    logger.info(m.toJson())
  }
}

class MeasurementReceiver extends Receiver {
  def receive(m: Measurement): Unit = {
  }
}

object Sensorl extends App {

  val server = new Server(6767)

  server.startServer()
}
