package io.simao.sensorl

import io.simao.librrd.LibRRD
import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.server.{DatabaseReceiver, Server}


object Sensorl extends App {
  val dbReceiverFn = (_: Unit) ⇒ {
    new DatabaseReceiver(MeasurementDatabase
      .withConnection("/home/simao/code/sensorl/lol.rrd"))
  }

  val updates = Array(
    "/home/simao/code/sensorl/lol.rrd",
    "920804700:12345",
    "920805000:12357",
    "920805300:12363"
  )

  println(LibRRD.rrdcreate(rrdArgs))
  println(LibRRD.rrdupdate(updates))

  val server = new Server(6767, dbReceiverFn)

  server.startServer()
}
