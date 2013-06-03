package ohnosequences.saws.signing.v3

import ohnosequences.saws.signing.Version


trait V3Input {
  def endpoint: String
  def headers: Traversable[(String, String)]
  def parameters: Traversable[(String, String)]
  def content: Array[Byte]
  def method: String
  def resource: String
  def region: String
  def service: String
}

case object v3 extends Version[V3Input, Map[String, String]]

