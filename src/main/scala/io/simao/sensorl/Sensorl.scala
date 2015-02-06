package io.simao.sensorl

import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.server.{DatabaseReceiver, Server}

object Sensorl extends App {
  val dbFilename = "/home/simao/code/sensorl/temp.rrd"
  
  val dbReceiverFn = (_: Unit) ⇒ {
    new DatabaseReceiver(MeasurementDatabase(dbFilename))
  }

  // MeasurementDatabase(dbFilename).setupDb(true)
  
  val server = new Server(6767, dbReceiverFn)

  server.startServer()
}
