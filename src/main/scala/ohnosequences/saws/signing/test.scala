package ohnosequences.saws.signing.buh

import ohnosequences.saws.signing._

import java.io.InputStream

sealed trait VersionAux {
  
  type Input
  // note that output here includes just what signing generates
  // not the original request, for example
  type Output
}

trait Version[I, O] extends VersionAux {
  
  type Input = I
  type Output = O
}

// it is probably a good idea to have richer types here, for Input and Output
// if we have that, then this type express what we want and we don't actually need to have
// Data[R] or something like that
case object v4 extends Version[
                                Map[String, String],
                                Map[String, String]
                              ]

// module with alternative impl, explicit I/O types
object altV4 {

  case class V4Input(
                      endpoint: String,
                      resource: String, 
                      headers: Map[String, String],
                      method: String,
                      content: InputStream
                    )

  // am I missing something here?
  case class V4Output(
                       date: String,
                       sha256: String                       
                     )

  case object v4 extends Version[V4Input, V4Output]

}

case object v3 extends Version[
                                Map[String, String],
                                Map[String, String]
                              ]



trait SigningProcessAux {

  type Version <: VersionAux
  // why not Input and Output? because they are already here, through Version :)
}

// here class just because I need a val
// abstract because I want objects implementing this
abstract class SigningProcess[V <: VersionAux](val v: V) extends SigningProcessAux {

  type Version = V

  def apply(input: v.Input, cred: Credentials): v.Output
}

case object signV4 extends SigningProcess(v4) {

  // stupid implementation
  // the only important detail here: types!
  def apply(input: v4.Input, cred: Credentials): v4.Output = Map(
                                                                  "lalala" -> "buuuh",
                                                                  "hey" -> "nooo"
                                                                )
}

trait SignerAux {

  type SigningProcess <: SigningProcessAux
}

// I'm not that sure if this is actually needed here
// it's basically nothing
case class Signer[
                    V <: VersionAux, 
                    S <: ohnosequences.saws.signing.buh.SigningProcess[V]
                 ](signingProcess: S) extends SignerAux {

  type SigningProcess = S

  def sign[R](req: R, cred: Credentials)
             (implicit 
               getFrom: R => signingProcess.v.Input,
               // this one is not essential, could be outside
               applyOutput: (R, signingProcess.v.Output) => R
             ): Signed[R] = 
    applyOutput(req, signingProcess(getFrom(req), cred)).asInstanceOf[Signed[R]]

  // possible feature here: tagged type for Signed request, including version
  // if you are afraid of this, forget it :)
  type Tagged[U] = { type Tag = U }
  type @@[T, U] = T with Tagged[U]
  type Signed[R] = R @@ signingProcess.v.type


}

object StupidThings {

  val x: Map[String, String] = signV4(Map("oh" -> "argh"), Credentials("asd3233rsa", "4134af5af"))

}