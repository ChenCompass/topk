package topk

import scala.util.{Failure, Success, Try}

object Cli {
  def main(args: Array[String]): Unit = {

    val usage = "Usage: <this> yyyy-MM-dd HH [yyyy-MM-dd HH]"
    if (args.length != 0 && args.length != 2 && args.length != 4 || args.headOption.contains("--help")){
      System.err.println(usage)
      System.exit(1)
    }

    // Load the blacklist
    val loadBlacklist = Try(Blacklist.getBlacklist("blacklist_domains_and_pages.txt"))
    if (loadBlacklist.isFailure) {
      System.err.println("Unable to read blacklist")
      System.exit(1)
    }
    val blacklist = loadBlacklist.getOrElse(Set.empty[String])
    System.out.println(s"Loaded blacklist of length ${blacklist.size}")

    TrustStore.create()
    TrustStore.load()

    val hours = ArgParser.parseArgs(args).toVector
    System.out.println(s"Hours in range: ${hours.length}")

    import ArgParser.fmt
    hours.foreach{h =>
      System.out.println(s"Starting work for ${fmt.print(h)}")
      Try(Worker.begin(h, blacklist)) match {
        case Success(_) =>
          System.out.println(s"Finished: ${fmt.print(h)}")
        case Failure(e) =>
          System.err.println(s"Failed: ${fmt.print(h)}")
          e.printStackTrace(System.err)
      }
    }
  }
}
