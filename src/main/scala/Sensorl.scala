import com.typesafe.scalalogging.LazyLogging

trait Receiver {
  def receive(m: Measurement): Unit
}

class LoggingReceiver extends Receiver with LazyLogging {
  override def receive(m: Measurement): Unit = {
    logger.info(m.toJson())
  }
}

class MeasurementReceiver extends Receiver {
  def receive(m: Measurement): Unit = {
  }
}

object Sensorl extends App {

  val server = new Server(6767)

  server.startServer()
}
