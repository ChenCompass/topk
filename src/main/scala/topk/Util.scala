package topk

import java.io.File
import java.net.{HttpURLConnection, URL}

import org.joda.time.DateTime

import scala.util.Try

object Util {

  def pad(x: Int): String = {
    if (x < 10) "0" + x.toString
    else x.toString
  }

  /** Generate input and output URI given a date
    *
    * @param year
    * @param month
    * @param day
    * @param hour
    * @return
    */
  def getUris(year: Int, month: Int, day: Int, hour: Int, outDir: Option[String] = None): (String,String) = {
    val mm = pad(month)
    val dd = pad(day)
    val hh = pad(hour)
    val in = s"https://dumps.wikimedia.org/other/pagecounts-all-sites/$year/$year-${mm}/pagecounts-${year}${mm}${dd}-${hh}0000.gz"
    val out = outDir match {
      case Some(d) =>
        d + File.separator + s"${year}${mm}${dd}-${hh}.txt"
      case _ =>
        s"${year}${mm}${dd}-${hh}.txt"
    }

    (in, out)
  }

  /** Extract fields from a DateTime
    *
    * @param dt
    * @return
    */
  def getFields(dt: DateTime = new DateTime()): (Int, Int, Int, Int) = {
    (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth, dt.getHourOfDay)
  }

  /** Retrieve a url as an InputStream
    *
    * @param url
    * @param connectTimeout
    * @param readTimeout
    * @param requestMethod
    * @return
    */
  def getUrlInputStream(url: String, connectTimeout: Int = 5000, readTimeout: Int = 5000, requestMethod: String = "GET") = {
    val u = new URL(url)
    val conn = u.openConnection.asInstanceOf[HttpURLConnection]
    HttpURLConnection.setFollowRedirects(false)
    conn.setConnectTimeout(connectTimeout)
    conn.setReadTimeout(readTimeout)
    conn.setRequestMethod(requestMethod)
    conn.connect()
    conn.getInputStream
  }

  /** Convert a line of input into a LogEntry object by extracting space separated fields
    *
    * @param line
    * @return
    */
  def parseLine(line: String): Option[LogEntry] = {
    Try{
      val s1 = line.indexOf(' ', 0)
      val s2 = line.indexOf(' ', s1 + 1)
      val s3 = line.indexOf(' ', s2 + 1)
      LogEntry(line.substring(0, s1), line.substring(s1 + 1, s2), line.substring(s2 + 1, s3).toInt)
    }.toOption
  }

}
