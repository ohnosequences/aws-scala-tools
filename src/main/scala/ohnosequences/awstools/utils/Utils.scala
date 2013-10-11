package ohnosequences.awstools.utils

object Utils {

  def waitForResource[A](resourceCheck: => Option[A]) : Option[A] = {
    var iteration = 1
    var current: Option[A] = None
    val limit = 50

    do {
      current = resourceCheck
      iteration += 1
      Thread.sleep(1000)
    } while (current.isEmpty && iteration < limit)

    current
  }

}
