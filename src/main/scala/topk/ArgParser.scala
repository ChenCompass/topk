package topk

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object ArgParser {
  val fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH")

  def parseArgs(args: Array[String]): HoursIterator = {

    // Read date range from command-line arguments
    val start = {
      for {
        dt <- args.lift(0)
        h <- args.lift(1)
      } yield {
        fmt.parseDateTime(dt + " " + h)
      }
    }.getOrElse(new DateTime().withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0))

    // Default to one hour range
    val end = {
      for {
        dt1 <- args.lift(2)
        h1 <- args.lift(3)
      } yield {
        fmt.parseDateTime(dt1 + " " + Util.pad(h1.toInt)).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
      }
    }
    HoursIterator(start, end)
  }
}
