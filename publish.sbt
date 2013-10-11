s3credentialsFile := Some("/home/evdokim/aws/era7.prop")

publishTo <<= (isSnapshot, s3credentials) {
                (snapshot,   credentials) =>
  val prefix = if (snapshot) "snapshots" else "releases"
  credentials map S3Resolver("Era7 "+prefix+" S3 bucket", "s3://"+prefix+".era7.com").toSbtResolver
}
