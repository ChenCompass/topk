package topk

case class LogEntry(domain_code: String, page_title: String, count_views: Int) extends Ordered [LogEntry] {

  def domain_code_and_page_title: String = domain_code + " " + page_title

  // return 0 if the same, negative if this < that, positive if this > that
  override def compare (that: LogEntry): Int = {
    if (this.count_views == that.count_views){
      if (this.domain_code_and_page_title > that.domain_code_and_page_title) 1
      else if (this.domain_code_and_page_title > that.domain_code_and_page_title) -1
      else 0
    } else if (this.count_views > that.count_views){
      1
    } else {
      -1
    }
  }
}
