package io.simao.sensorl

import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.server.{DatabaseReceiver, Server}


object Sensorl extends App {
  val db = "/home/simao/code/sensorl/temp.rrd"

  val dbReceiverFn = (_: Unit) ⇒ {
    new DatabaseReceiver(MeasurementDatabase
      .withConnection(db))
  }

  MeasurementDatabase.withConnection(db)(_.setupDb(true))

  val server = new Server(6767, dbReceiverFn)

  server.startServer()
}
