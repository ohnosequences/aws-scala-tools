package ohnosequences.awstools

import scala.reflect.runtime._

package object test {

  val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)

  def reflectName(fullName: String): String = {
    val module = runtimeMirror.staticModule(fullName)
    val obj = runtimeMirror.reflectModule(module)
    obj.instance.toString
  }

  def allInstancesToString(prefix: String): Set[String] = {
    runtimeMirror
      .staticClass(prefix)
      .knownDirectSubclasses
      .map { symbol =>
        symbol.fullName.stripPrefix(s"${prefix}.")
      }
  }
}
