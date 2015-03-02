package io.simao.util

import org.joda.time.DateTime

class DateTimeToEpoch(value: DateTime) {
  def toEpoch: Long = value.getMillis / 1000l
}

object DateTimeToEpoch {
  implicit def toDateTimeEpoch(value: DateTime): DateTimeToEpoch = new DateTimeToEpoch(value)
}
