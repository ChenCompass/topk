package topk

import java.nio.file.{Files, Paths}

import scala.io.Codec

object Blacklist {

  def getBlacklist(f: String): Set[String] = {
    val blackListPath = Paths.get(f)
    val blackListUri = "https://s3.amazonaws.com/dd-interview-data/data_engineer/wikipedia/blacklist_domains_and_pages"
    if (!blackListPath.toFile.exists()){
      System.out.println(s"Downloading blacklist from ${blackListUri}")
      val is = Util.getUrlInputStream(blackListUri)
      Files.copy(is, blackListPath)
      System.out.println("Done.")
    }

    val lines = scala.io.Source.fromFile(f)(Codec.UTF8).getLines()
    lines.toSet
  }

}
