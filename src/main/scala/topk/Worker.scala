package topk

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.util.zip.GZIPInputStream

import org.joda.time.DateTime

import scala.collection.mutable
import scala.io.Codec

object Worker {

  def begin(h: DateTime, blacklist: Set[String], outDirOpt: Option[String] = None): Unit = {
    val (yr, mo, day, hour) = Util.getFields(h)
    val (in, out) = Util.getUris(yr, mo, day, hour, outDirOpt)

    val outPath = Paths.get(out)
    if (!outPath.toFile.exists()){
      val topK = Worker.fetch(in, blacklist)
      val w = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)
      System.out.println(s"Writing results to ${outPath.toFile.getName}")

      topK.foreach{t =>
        t._2.foreach{e =>
          w.write(t._1)
          w.write("\t")
          w.write(e.page_title)
          w.write("\t")
          w.write(e.count_views.toString)
          w.write("\n")
        }
      }
      w.flush()
      w.close()
    } else {
      System.out.println(s"Skipping ${yr}-${mo}-${day} ${hour} because ${outPath.toFile.getName} already exists")
    }
  }

  /** Decompress gzipped input stream
    *
    * @param in
    * @param blacklist
    * @param k
    * @return
    */
  def fetch(in: String, blacklist: Set[String], k: Int = 25): Map[String,Vector[LogEntry]] = {
    System.out.println(s"Reading from ${in}")
    val is = Util.getUrlInputStream(in)
    val gzis = new GZIPInputStream(is, 65536)
    val lines = scala.io.Source.fromInputStream(gzis)(Codec.UTF8).getLines()
    val topK = buildMap(lines, blacklist, k)
    is.close()
    gzis.close()
    topK
  }

  /** Testable function to build TopK map from line iterator
    *
    * @param lines
    * @param blacklist
    * @param k
    * @return
    */
  def buildMap(lines: Iterator[String], blacklist: Set[String], k: Int = 25): Map[String,Vector[LogEntry]] = {
    val entries = lines.flatMap(Util.parseLine)

    // Initialize data structure for Top K by domain
    val sets = mutable.Map[String, (Int, mutable.SortedSet[LogEntry])]()

    var i = 0
    var j = 0
    var total: Long = 0
    entries.foreach {l =>
      if (blacklist.contains(l.domain_code_and_page_title)){
        j += 1
        if (j % 1000 == 0) System.out.println(s"Ignored ${j} entries due to blacklist")
      } else {
        total += l.count_views

        // Get the SortedSet and its current size
        sets.get(l.domain_code) match {
          case Some((n, s)) =>
            if (l.count_views >= s.min.count_views){
              s.add(l)
              if (n >= k) s.remove(s.min) // Remove the minimum entry only if we are out of space
              sets.update(l.domain_code, (n + 1, s))
            }
          case _ => // Initialize and add to the map if it doesn't already exist
            val s1 = mutable.SortedSet[LogEntry]()
            s1.add(l)
            sets.update(l.domain_code, (1, s1))
        }

        i += 1
        if (i % 1000000 == 0) System.out.println(s"Processed ${i} entries")
      }
    }

    System.out.println(s"Finished reading. Total views=${total}")

    // Convert to immutable data structure before returning
    sets.map{x =>
      (x._1, x._2._2.toVector)
    }.toMap
  }

}
