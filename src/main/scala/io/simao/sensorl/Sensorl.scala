package io.simao.sensorl

import java.io.File

import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.http.HttpServer
import io.simao.sensorl.server.{DatabaseReceiver, MetricsServer}

object Sensorl extends App {
  implicit val ec = scala.concurrent.ExecutionContext.global

  val dbFilename = "temp.rrd"
  
  val dbReceiverFn = (_: Unit) ⇒ {
    new DatabaseReceiver(MeasurementDatabase(dbFilename))
  }

  MeasurementDatabase(dbFilename).setupDb(false)
  
  MeasurementDatabase(dbFilename).graph(new File("/home/simao/code/sensorl/test-c.png"))

  val server = new MetricsServer(6767, dbReceiverFn)
  val httpServer = new HttpServer(new File("http/"), 8080)

  httpServer.start()
  server.startServer()
}
