package ohnosequences.saws.signing

trait Signer {
  type Version <: SigningVersion
  def sign[R](request: R, credentials: Credentials)(data: Version#Data[R]): Version#Output
}
