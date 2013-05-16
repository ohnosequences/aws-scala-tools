package ohnosequences.saws.signing.v4

import ohnosequences.saws.signing.SigningVersion

trait V4 extends SigningVersion {
  type Output = Map[String, String]
  type Data[R] = V4Data[R]
}
