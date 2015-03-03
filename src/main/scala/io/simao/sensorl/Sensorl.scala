package io.simao.sensorl

import java.io.File

import io.simao.librrd.LibRRD
import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.http.HttpServer
import io.simao.sensorl.server.{LoggingReceiver, DatabaseReceiver, MetricsServer}

object Sensorl extends App {
  implicit val ec = scala.concurrent.ExecutionContext.global


  // TODO: Holy shit how to do this?
  val dbFilename = "temp.rrd"

//  val dbReceiverFn = (_: Unit) ⇒ {
//    new DatabaseReceiver(MeasurementDatabase(dbFilename))
//  }

  val server = new MetricsServer(6767, Unit ⇒ new LoggingReceiver)
  val httpServer = new HttpServer(new File("http/"), 8080)

  httpServer.start()
  server.startServer()
}

