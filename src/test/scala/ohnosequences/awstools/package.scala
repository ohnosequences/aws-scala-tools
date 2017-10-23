package ohnosequences.awstools

import scala.reflect.runtime._

package object test {

  val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def reflectObject[T](fullName: String): T = {
    val module = runtimeMirror.staticModule(fullName)
    val obj = runtimeMirror.reflectModule(module)
    obj.instance.asInstanceOf[T]
  }

  def reflectName(fullName: String): String =
    reflectObject(fullName).toString

  def allInstances(className: String): Set[String] = {
    runtimeMirror
      .staticClass(className)
      .knownDirectSubclasses
      .map { _.fullName }
  }
}
