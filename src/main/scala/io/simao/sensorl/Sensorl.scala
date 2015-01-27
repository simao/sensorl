package io.simao.sensorl

import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.server.{DatabaseReceiver, Server}


object Sensorl extends App {
  val dbReceiverFn = (_: Unit) ⇒ {
    new DatabaseReceiver(MeasurementDatabase.withConnection("jdbc:sqlite:measurements.db"))
  }

  val server = new Server(6767, dbReceiverFn)

  server.startServer()
}
