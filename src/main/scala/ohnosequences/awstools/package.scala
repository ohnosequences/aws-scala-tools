package ohnosequences

package object awstools {

  type Token = String


  def rotateTokens[Result](
    makeRequest: Option[Token] => (Option[Token], Seq[Result])
  ): Seq[Result] = {

    @scala.annotation.tailrec
    def rotateTokens_rec(
      token: Option[Token],
      acc: Seq[Result]
    ): Seq[Result] = {

      if (token.isEmpty) acc
      else {
        val (newToken, newChunk) = makeRequest(token)
        rotateTokens_rec(newToken, newChunk ++ acc)
      }
    }

    val (firstToken, firstChunk) = makeRequest(None)
    rotateTokens_rec(firstToken, firstChunk)
  }

  def rotateTokensLazily[Result](
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

}
