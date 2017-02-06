package topk

class WorkerSpec extends UnitSpec {

  def fixture = new {

    val blacklist = Set[String](
      "cc Special:Blacklisted"
    )

    val lines = Vector(
      "aa Special:PageA 1 345" // Should be excluded from Top3
      ,"aa Special:PageB 999 789"
      ,"aa Special:PageC 5 12"
      ,"aa Special:PageD 42 0"
      ,"cc Special:Blacklisted 15 0" // Should be blacklisted
    )

    val ans = Map[String,Vector[LogEntry]](
      "aa" -> Vector[LogEntry](
        LogEntry("aa", "Special:PageC", 5),
        LogEntry("aa", "Special:PageD", 42),
        LogEntry("aa", "Special:PageB", 999)
      )
    )

  }

  "Worker" should "build TopK Map" in {
    val f = fixture
    import f._

    Worker.buildMap(lines.iterator, blacklist, 3) should be (ans)
  }
}
