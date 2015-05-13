import sbt._
import Keys._
import com.amazonaws.auth._

object AWSToolsBuild extends Build {

  val testCredentialsProvider = SettingKey[Option[AWSCredentialsProvider]]("credentials provider for test environment")

  override lazy val settings = super.settings ++ Seq(testCredentialsProvider := None)

  def providerConstructorPrinter(provider: AWSCredentialsProvider) = provider match {
    case ip: InstanceProfileCredentialsProvider => {
      "new com.amazonaws.auth.InstanceProfileCredentialsProvider()"
    }
    case ep: EnvironmentVariableCredentialsProvider => {
      "new com.amazonaws.auth.EnvironmentVariableCredentialsProvider()"
    }
    case pp: PropertiesFileCredentialsProvider => {
      val field = pp.getClass().getDeclaredField("credentialsFilePath")
      field.setAccessible(true)
      val path = field.get(pp).toString
      "new com.amazonaws.auth.PropertiesFileCredentialsProvider(\"\"\"$path$\"\"\")".replace("$path$", path)
    }

    //todo fix!
    case p => ""
  }

  lazy val root = Project(id = "aws",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      sourceGenerators in Test += task[Seq[File]] {
        println("generation credentials for tests")
        val text = """
                     |package generated.test
                     |
                     |object credentials {
                     |  val credentialsProvider: Option[com.amazonaws.auth.AWSCredentialsProvider] = $cred$
                     |}
                     |""".stripMargin
          .replace("$cred$", testCredentialsProvider.value.map(providerConstructorPrinter).toString)
        val file = (sourceManaged in Compile).value / "testCredentials.scala"
        IO.write(file, text)
        Seq(file)
      }
    )
  )
}