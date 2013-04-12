package ohnosequences.awstools.ec2


import scala.collection.JavaConversions._

case class Instance(ec2: EC2, instanceId: String) {

  def terminate() {
    ec2.terminateInstance(instanceId)
  }

  def createTag(tag: ohnosequences.awstools.ec2.Tag) {
    ec2.createTags(instanceId, List(tag))
  }

  def createTags(tags: List[ohnosequences.awstools.ec2.Tag]) {
    ec2.createTags(instanceId, tags)
  }

//  def getTagValue(tagName: String) = {
//     ec2.ec2.describeTags(new DescribeTagsRequest()
//      .withFilters(List(
//        ResourceFilter(instanceId).toEC2Filter,
//        TagFilter(Tag(tagName, "*")).toEC2Filter
//      ))
//    ).getTags.find(_.getKey == tagName).map(_.getValue)
//  }

  def getTagValue(tagName: String) = {
    val instance = ec2.getEC2InstanceById(instanceId).get
    instance.getTags.find(_.getKey == tagName).map(_.getValue)
  }


  def getInstanceId = instanceId

  def getSSHCommand(): String = {
    val instance = ec2.getEC2InstanceById(instanceId).get
    val keyPairFile = instance.getKeyName + ".pem"
    val publicDNS = instance.getPublicDnsName
    "ssh -i " + keyPairFile + " ec2-user@" + publicDNS
  }

  def getState(): String = {
    val instance = ec2.getEC2InstanceById(instanceId).get
    instance.getState.getName

  }

  def getPublicDNS(): String = {
    ec2.getEC2InstanceById(instanceId).get.getPublicDnsName
  }

}


