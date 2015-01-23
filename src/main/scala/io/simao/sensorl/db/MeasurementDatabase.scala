package io.simao.sensorl.db


import java.sql.{Connection, DriverManager}
import io.simao.sensorl.message.Measurement
import io.simao.util.KestrelObj._
import org.joda.time.DateTime

object MeasurementDatabase {
  def apply(jdbcString: String) = {
    val connection = DriverManager.getConnection(jdbcString)
    new MeasurementDatabase(connection)
  }

  def withConnection[T](jdbcString: String)(f: MeasurementDatabase ⇒ T) = {
    val connection = DriverManager.getConnection(jdbcString)
    val db = new MeasurementDatabase(connection)
    try f(db)
    finally connection.close()
  }
}

class MeasurementDatabase(connection: Connection) {
  def save(item: Measurement): Measurement = {
    item.tap { i ⇒
      val statement = connection.createStatement()
      val now = DateTime.now().toString
      statement.executeUpdate(s"insert into measurements values('${item.mid}', '${item.value}', '${item.time}')")
    }
  }

  def setupTables(drop: Boolean = false): Connection = {
    connection.tap { c ⇒
      val statement = c.createStatement()
      if (drop)
        statement.executeUpdate("drop table if exists measurements")
      statement.executeUpdate("create table if not exists measurements (mid integer, value float, updated_at string)")
    }
  }
}
