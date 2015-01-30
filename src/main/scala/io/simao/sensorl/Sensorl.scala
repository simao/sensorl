package io.simao.sensorl

import io.simao.librrd.LibRRD
import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.server.{RRDToolReceiver, DatabaseReceiver, Server}


object Sensorl extends App {
  val dbReceiverFn = (_: Unit) ⇒ {
    new DatabaseReceiver(MeasurementDatabase.withConnection("jdbc:sqlite:measurements.db"))
  }

  println(LibRRD.rrdcreate())

  val rrdToolReceiverFn = (_: Unit) ⇒ { new RRDToolReceiver }

  val server = new Server(6767, rrdToolReceiverFn)

  server.startServer()
}
