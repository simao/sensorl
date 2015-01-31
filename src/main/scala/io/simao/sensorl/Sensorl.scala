package io.simao.sensorl

import io.simao.librrd.LibRRD
import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.server.{RRDToolReceiver, DatabaseReceiver, Server}


object Sensorl extends App {
  val dbReceiverFn = (_: Unit) ⇒ {
    new DatabaseReceiver(MeasurementDatabase.withConnection("jdbc:sqlite:measurements.db"))
  }

  val rrdArgs = Array(
    "/home/simao/code/sensorl/lol.rrd",
    "--start", "920804400",
    "DS:speed:COUNTER:600:U:U",
    "RRA:AVERAGE:0.5:1:24",
    "RRA:AVERAGE:0.5:6:10"
  )

  val updates = Array(
    "/home/simao/code/sensorl/lol.rrd",
    "920804700:12345",
    "920805000:12357",
    "920805300:12363"
  )

  println(LibRRD.rrdcreate(rrdArgs))
  println(LibRRD.rrdupdate(updates))

  val rrdToolReceiverFn = (_: Unit) ⇒ { new RRDToolReceiver }

  val server = new Server(6767, rrdToolReceiverFn)

  server.startServer()
}
