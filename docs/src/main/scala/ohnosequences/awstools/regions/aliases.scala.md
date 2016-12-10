
```scala
package ohnosequences.awstools.regions

import com.amazonaws.regions._
```

## Geographical aliales

  This sealed class reflects the `Regions` from the SDK, but allows us to have
  - convenient aliases based on the geographical locations
  - precise type for each region and dispatch on it in implicit resolution (see AMIs code)

  This type is also implicitly converted to `Region`, `Regions` and `AwsRegionProvider` types, so you can use it anywhere those types are expected (once you've imported `ohnosequences.awstools.regions._`)


```scala
sealed abstract class RegionAlias(val region: Regions) {

  override def toString: String = region.getName
}
```

- Asia Pacific

```scala
case object Tokyo              extends RegionAlias(Regions.AP_NORTHEAST_1)
case object Seoul              extends RegionAlias(Regions.AP_NORTHEAST_2)
case object Mumbai             extends RegionAlias(Regions.AP_SOUTH_1)
case object Singapore          extends RegionAlias(Regions.AP_SOUTHEAST_1)
case object Sydney             extends RegionAlias(Regions.AP_SOUTHEAST_2)
```

- China

```scala
case object Beijing            extends RegionAlias(Regions.CN_NORTH_1)
```

- Europe

```scala
case object Frankfurt          extends RegionAlias(Regions.EU_CENTRAL_1)
case object Ireland            extends RegionAlias(Regions.EU_WEST_1)
```

- Somewhere in CIA

```scala
case object GovCloud           extends RegionAlias(Regions.GovCloud)
```

- South America

```scala
case object SaoPaulo           extends RegionAlias(Regions.SA_EAST_1)
```

- US East

```scala
case object NorthernVirginia   extends RegionAlias(Regions.US_EAST_1)
case object Ohio               extends RegionAlias(Regions.US_EAST_2)
```

- US West

```scala
case object NorthernCalifornia extends RegionAlias(Regions.US_WEST_1)
case object Oregon             extends RegionAlias(Regions.US_WEST_2)

```




[main/scala/ohnosequences/awstools/autoscaling/client.scala]: ../autoscaling/client.scala.md
[main/scala/ohnosequences/awstools/autoscaling/filters.scala]: ../autoscaling/filters.scala.md
[main/scala/ohnosequences/awstools/autoscaling/package.scala]: ../autoscaling/package.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/client.scala]: ../ec2/client.scala.md
[main/scala/ohnosequences/awstools/ec2/instances.scala]: ../ec2/instances.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala]: ../ec2/InstanceType-AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/package.scala]: ../package.scala.md
[main/scala/ohnosequences/awstools/regions/aliases.scala]: aliases.scala.md
[main/scala/ohnosequences/awstools/regions/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: ../s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: ../s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: ../s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: ../s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/client.scala]: ../sns/client.scala.md
[main/scala/ohnosequences/awstools/sns/package.scala]: ../sns/package.scala.md
[main/scala/ohnosequences/awstools/sns/subscribers.scala]: ../sns/subscribers.scala.md
[main/scala/ohnosequences/awstools/sns/topics.scala]: ../sns/topics.scala.md
[main/scala/ohnosequences/awstools/sqs/client.scala]: ../sqs/client.scala.md
[main/scala/ohnosequences/awstools/sqs/messages.scala]: ../sqs/messages.scala.md
[main/scala/ohnosequences/awstools/sqs/package.scala]: ../sqs/package.scala.md
[main/scala/ohnosequences/awstools/sqs/queues.scala]: ../sqs/queues.scala.md
[test/scala/ohnosequences/awstools/autoscaling.scala]: ../../../../../test/scala/ohnosequences/awstools/autoscaling.scala.md
[test/scala/ohnosequences/awstools/instanceTypes.scala]: ../../../../../test/scala/ohnosequences/awstools/instanceTypes.scala.md
[test/scala/ohnosequences/awstools/package.scala]: ../../../../../test/scala/ohnosequences/awstools/package.scala.md
[test/scala/ohnosequences/awstools/sqs.scala]: ../../../../../test/scala/ohnosequences/awstools/sqs.scala.md