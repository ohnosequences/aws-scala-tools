package ohnosequences.awstools.ec2

object Utils {
  def base64encode(input: String) = new sun.misc.BASE64Encoder().encode(input.getBytes())
}
