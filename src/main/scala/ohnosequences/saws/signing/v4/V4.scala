package ohnosequences.saws.signing.v4

import ohnosequences.saws.signing.{VersionAux, Version}


trait V4Input {
  def endpoint: String
  def headers: Traversable[(String, String)]
  def parameters: Traversable[(String, String)]
  def content: Array[Byte]
  def method: String
  def resource: String
  def region: String
  def service: String
}

case object v4 extends Version[V4Input, Map[String, String]]

