package ohnosequences.awstools.ec2

object Utils {
  def base64encode(input: String) = new sun.misc.BASE64Encoder().encode(input.getBytes())

  def stringToOption(s: String): Option[String] = {
    if(s == null || s.isEmpty) {
      None
    } else {
      Some(s)
    }

  }


}
