package ohnosequences.awstools

object TestCredentials {
  val aws: Option[AWSClients] = {
    generated.test.credentials.credentialsProvider.map (AWSClients.create(_))
  }
}
