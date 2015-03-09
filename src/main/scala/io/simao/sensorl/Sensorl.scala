package io.simao.sensorl

import java.io.File

import io.simao.librrd.LibRRD
import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.http.HttpServer
import io.simao.sensorl.server.{LoggingReceiver, DatabaseReceiver, MetricsServer}

object Sensorl extends App {
  implicit val ec = scala.concurrent.ExecutionContext.global

  val server = new MetricsServer(6767, Unit ⇒ new DatabaseReceiver)
  val httpServer = new HttpServer(new File("http/"), 8080)

  httpServer.start()
  server.startServer()
}

