package ohnosequences

import java.util.Base64
import java.nio.charset.StandardCharsets.UTF_8

package object awstools {

  type Token = String

  def rotateTokens[Result](
    makeRequest: Option[Token] => (Option[Token], Seq[Result])
  ): Stream[Result] = {

    def rotateTokens_rec(token: Option[Token]): Stream[Result] = {

      if (token.isEmpty) Stream.empty
      else {
        val (nextToken, chunk) = makeRequest(token)
        chunk.toStream #::: rotateTokens_rec(nextToken)
      }
    }

    val (firstToken, firstChunk) = makeRequest(None)
    firstChunk.toStream #::: rotateTokens_rec(firstToken)
  }

  // def rotate[
  //   Request <: AmazonWebServiceRequest,
  //   Response <: AmazonWebServiceResult[ResponseMetadata],
  //   Result
  // ](baseRequest: Request,
  //   makeRequest: Request => Response
  // )(withToken: Request => (Token => Request),
  //   getNextToken: Request => Token,
  //   getResults: Response => Seq[Result]
  // ): Stream[Result] = {
  //
  //   def fromResponse(response: Response) = (
  //     // NOTE: next token is null if there's nothing more to list
  //     Option(getNextToken(response)),
  //     getResults(response)
  //   )
  //
  //   rotateTokens { token =>
  //     fromResponse(makeRequest(
  //       token.fold(baseRequest)(withToken(baseRequest))
  //     ))
  //   }
  // }

  implicit class StringAWSOps(val str: String) extends AnyVal {

    def encodeBase64: String = new String(Base64.getEncoder().encode(str.getBytes(UTF_8)), UTF_8)
    def decodeBase64: String = new String(Base64.getDecoder().decode(str.getBytes(UTF_8)), UTF_8)
  }

}
