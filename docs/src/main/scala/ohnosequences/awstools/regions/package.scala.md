
```scala
package ohnosequences.awstools

import com.amazonaws.regions._

package object regions {
```

Adding SDK types to the scope without SDK imports:

```scala
  type Regions = com.amazonaws.regions.Regions
  type Region  = com.amazonaws.regions.Region
  type AwsRegionProvider = com.amazonaws.regions.AwsRegionProvider
  type DefaultAwsRegionProviderChain = com.amazonaws.regions.DefaultAwsRegionProviderChain
```

### Implicits
- `Regions` enum → `Region` and `AwsRegionProvider`

```scala
  implicit def RegionsToRegion(regions: Regions): Region = Region.getRegion(regions)
  implicit def RegionsToProvider(region: Regions): AwsRegionProvider = RegionToProvider(region)
```

- `Region` type ⇄ `AwsRegionProvider`

```scala
  implicit def RegionToProvider(region: Region): AwsRegionProvider = new AwsRegionProvider {
    override def getRegion(): String = region.getName
  }
  implicit def ProviderToRegion(provider: AwsRegionProvider): Region = Regions.fromName(provider.getRegion)
```

- `RegionAlias` → each of the other three

```scala
  implicit def RegionAliasToRegions (alias: RegionAlias): Regions = alias.region
  implicit def RegionAliasToRegion  (alias: RegionAlias): Region  = alias.region
  implicit def RegionAliasToProvider(alias: RegionAlias): AwsRegionProvider = alias.region

}

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