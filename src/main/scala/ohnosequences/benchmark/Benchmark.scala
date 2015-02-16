package ohnosequences.benchmark

import scala.collection.mutable

trait AnyBench {
  def register(label: String, time: Long): Unit

  def averageTime(label: String): Double

  def averageSpeed(label: String): Double

  def totalTime(label: String): Long
}

class Bench extends AnyBench {
  val counts = new mutable.HashMap[String, Int]()
  val times = new mutable.HashMap[String, Long]()

  override def register(label: String, time: Long): Unit = {
    counts.get(label) match {
      case None => counts.put(label, 1)
      case Some(c) => counts.put(label, c + 1)
    }

    times.get(label) match {
      case None => times.put(label, time)
      case Some(t) => times.put(label, t + time)
    }
  }

  override def averageTime(label: String): Double = {
    val time = times.getOrElse(label, 0L)
    val count = counts.getOrElse(label, 0)

    if(count == 0) {
      Double.NaN
    } else {
      (time + 0.0) / count
    }
  }

  override def averageSpeed(label: String): Double = {
    val time = times.getOrElse(label, 0L)
    val count = counts.getOrElse(label, 0)

    if(time == 0L) {
      Double.NaN
    } else {
      (count + 0.0) / time
    }
  }

  override def totalTime(label: String): Long = {
    times.getOrElse(label, 0L)
  }
}
