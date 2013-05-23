package ohnosequences.saws.signing


sealed trait VersionAux {
  type Input
  type Output
}

trait Version[I, O] extends VersionAux {
  type Input = I
  type Output = O
}


trait SigningProcessAux {
  type Version <: VersionAux
}


abstract class SigningProcess[V <: VersionAux](val v: V) extends SigningProcessAux {
  type Version = V
  def apply(input: v.Input, credentials: Credentials): v.Output
}

trait SignerAux {
  type SigningProcess <: SigningProcessAux
}


case class Signer[V <: VersionAux, S <: ohnosequences.saws.signing.SigningProcess[V]](signingProcess: S) {

  type SigningProcess = S

  def sign[R](req: R, cred: Credentials)(implicit
    getFrom: R => signingProcess.v.Input,
    applyOutput: (R, signingProcess.v.Output) => R
  ): R = applyOutput(req, signingProcess.apply(getFrom(req), cred))

}
