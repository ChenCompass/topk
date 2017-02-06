package topk

import org.joda.time.DateTime

class HoursIteratorSpec extends UnitSpec{
  def fixture = new {
    val start = new DateTime().withDate(2016, 8, 2).withHourOfDay(0)
    val end = new DateTime().withDate(2016, 8, 2).withHourOfDay(1)
    val end1 = new DateTime().withDate(2016, 8, 3).withHourOfDay(0)
  }

  "HoursIterator" should "produce an iterator of DateTime for each hour between two DateTimes" in {
    val f = fixture
    import f._
    HoursIterator(start, Some(end)).length should be (1)
    HoursIterator(start, Some(end1)).length should be (24)
  }


}
