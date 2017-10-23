package ohnosequences.awstools.test

import org.scalatest._, AppendedClues._
import ohnosequences.awstools._, ec2._, regions._
import com.amazonaws.services.ec2.model._
import scala.collection.JavaConverters._

class AMIs extends FunSuite {

  val allAMIs: Set[AnyAmazonLinuxAMI] =
    allInstances("ohnosequences.awstools.ec2.AmazonLinuxAMI").map(reflectObject[AnyAmazonLinuxAMI])

  allAMIs
    // NOTE: we need to create a client per region
    .groupBy(_.region)
    .foreach { case (region: RegionAlias, amis) =>
      region match {
        case GovCloud | Beijing =>
          ignore(s"Cannot access [${region}] region") {}
        case _ => {
          test(s"Check AMIs in [${region}] region") {

            val client = ec2.clientBuilder
              .withCredentials(new com.amazonaws.auth.DefaultAWSCredentialsProviderChain())
              .withRegion(region)
              .build

            amis.foreach { ami =>
              info(s"${ami.id}:  ${ami.arch}  ${ami.virt}\t${ami.storage}")

              val imageOpt: Option[Image] = client.describeImages(
                new DescribeImagesRequest().withImageIds(ami.id)
              ).getImages.asScala.headOption

              assert { imageOpt.nonEmpty }

              imageOpt.map { img =>

                assert {
                  img.getArchitecture == ami.arch.toString
                } withClue "Image architecture doesn't coincide"

                assert {
                  img.getState == "available"
                } withClue "Image is not available"

                assert {
                  img.getImageOwnerAlias == "amazon"
                } withClue "Image owner is not Amazon"

                assert {
                  img.getDescription.startsWith(s"Amazon Linux AMI ${ami.version}")
                } withClue "Image description doesn't concide name + version)"

                assert {
                  img.getRootDeviceType == {
                    (ami.storage: AnyStorageType) match {
                      case EBS           => "ebs"
                      case InstanceStore => "instance-store"
                    }
                  }
                } withClue "Image storage type doesn't concide"

                assert {
                  img.getVirtualizationType == {
                    (ami.virt: AnyVirtualization) match {
                      case HVM => "hvm"
                      case PV  => "paravirtual"
                    }
                  }
                } withClue "Image virtualization type doesn't concide"
              }
            }
          }
        }
      }

  }

}
