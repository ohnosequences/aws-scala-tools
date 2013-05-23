package ohnosequences.saws.signing.v2

import ohnosequences.saws.signing.{VersionAux, Version}


trait V2Input {
  def endpoint: String
  def parameters: Traversable[(String, String)]
  def resource: String
}

case object v2 extends Version[V2Input, Map[String, String]]

