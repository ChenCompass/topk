package topk

import org.joda.time._

case class HoursIterator(start: DateTime, end: Option[DateTime]) extends Iterator[DateTime] {
  private val startHour = start.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
  private val endHour = end.getOrElse(startHour.plusHours(1)).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
  private var current = startHour

  override def hasNext: Boolean = {
    current.isBefore(endHour) && !current.isEqual(endHour)
  }

  override def next(): DateTime = {
    if (!hasNext){
      throw new NoSuchElementException()
    } else {
      val ret = current
      current = current.plusHours(1)
      ret
    }
  }

}
