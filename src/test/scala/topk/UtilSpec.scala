package topk

class UtilSpec extends UnitSpec {
  def fixture = new {
    val lines = Vector(
      "aa Special:PageA 1 345"
      ,"aa Special:PageB 999 789"
      ,"aa Special:PageC ABC 789" // Should fail due to characters
      ,"aa Special:PageD" // Should fail due to missing field
    )
    val ans = Vector(
      LogEntry("aa", "Special:PageA", 1),
      LogEntry("aa", "Special:PageB", 999)
    )
  }

  "LineParser" should "Extract LogEntry" in {
    val f = fixture
    import f._

    Util.parseLine(lines.head) should be (Some(ans.head))
  }

  it should "Extract multiple LogEntry" in {
    val f = fixture
    import f._
    lines.flatMap(Util.parseLine) should be (ans)
  }

}
