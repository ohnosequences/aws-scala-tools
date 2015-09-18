package ohnosequences.awstools

import com.amazonaws.auth._, profile._

case object test {

  lazy val awsClients: AWSClients = AWSClients.create(new DefaultAWSCredentialsProviderChain())
}
