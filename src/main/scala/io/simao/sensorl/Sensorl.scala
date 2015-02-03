package io.simao.sensorl

import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.server.{DatabaseReceiver, Server}

object Sensorl extends App {
  val dbFilename = "temp.rrd"
  
  val dbReceiverFn = (_: Unit) ⇒ {
    new DatabaseReceiver(MeasurementDatabase(dbFilename))
  }

  MeasurementDatabase(dbFilename).setupDb(false)
  
  val server = new Server(6767, dbReceiverFn)

  server.startServer()
}
