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

class DatabaseReceiver(withDbFn: ((MeasurementDatabase) ⇒ Unit) ⇒ Unit) extends Receiver with LazyLogging {
  def receive(m: Measurement): Unit = {
    logger.info("new measurement" + m.toJson(4))
    withDbFn (_.save(m))
  }
}

object Sensorl extends App {
  val dbReceiverFn = (_: Unit) ⇒ {
    new DatabaseReceiver(MeasurementDatabase.withConnection("jdbc:sqlite:measurements.db"))
  }

  val server = new Server(6767, dbReceiverFn)

  server.startServer()
}
