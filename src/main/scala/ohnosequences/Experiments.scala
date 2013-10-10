
import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.identitymanagement.model._
import com.amazonaws.services.s3.{S3ClientOptions, AmazonS3Client}
import java.io.File


object S3GlobalTest {
  def main2(args: Array[String]) {
    val credentials = new PropertiesCredentials(new File("/home/evdokim/ohno.prop"))
    val s3Client = new AmazonS3Client(credentials)
    s3Client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true))

    s3Client.setRegion(Region.getRegion(Regions.EU_WEST_1))

    println(s3Client.listObjects("singaporefruit").getCommonPrefixes)

  }

}

object PolicyCreator {

  def main(args: Array[String]) {
    //new StaticCredentialsProvider()
    val credentials = new PropertiesCredentials(new File("/home/evdokim/ohno.prop"))

    val role = "nispero"
    val iam = new com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient(credentials)


    val a = """{"Version":"2008-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":["ec2.amazonaws.com"]},"Action":["sts:AssumeRole"]}]}"""
    val p = """{"Statement":[{"Effect":"Allow","Action":"*","Resource":"*"}]}"""


    try {
      iam.deleteRolePolicy(new DeleteRolePolicyRequest()
        .withRoleName(role)
        .withPolicyName(role)
      )
    } catch {
      case e: NoSuchEntityException => ()
    }

    try {
      iam.removeRoleFromInstanceProfile(new RemoveRoleFromInstanceProfileRequest()
        .withInstanceProfileName(role)
        .withRoleName(role)
      )
    } catch {
      case e: NoSuchEntityException => ()
    }

    try {
      iam.deleteInstanceProfile(new DeleteInstanceProfileRequest()
        .withInstanceProfileName(role)
      )
    } catch {
      case e: NoSuchEntityException => ()
    }

    try {
      iam.deleteRole(new DeleteRoleRequest()
        .withRoleName(role)
      )
    } catch {
      case e: NoSuchEntityException => ()
    }

    try {
      iam.createInstanceProfile(new CreateInstanceProfileRequest()
        .withInstanceProfileName(role)
      )
    } catch {
      case e: EntityAlreadyExistsException => println("already exist");
    }

    try {
      iam.createRole(new CreateRoleRequest()
        .withRoleName(role)
        .withAssumeRolePolicyDocument(a)
      )
    } catch {
      case e: EntityAlreadyExistsException => println("already exist");
    }

    try {
      iam.putRolePolicy(new PutRolePolicyRequest()
        .withPolicyName(role)
        .withRoleName(role)
        .withPolicyDocument(p)
      )
    } catch {
      case e: EntityAlreadyExistsException => println("already exist");
    }

    iam.addRoleToInstanceProfile(new AddRoleToInstanceProfileRequest()
      .withInstanceProfileName(role)
      .withRoleName(role)
    )

    val arn = iam.getInstanceProfile(new GetInstanceProfileRequest()
      .withInstanceProfileName(role)
    ).getInstanceProfile.getArn

    println(arn)

  }


  def createGodRole(name: String) {


  }
}



//package ohnosequences
//
//import ohnosequences.awstools.s3.S3
//import java.io.File
//import ohnosequences.awstools.ec2.EC2
//import com.amazonaws.services.ec2.model.{DescribeReservedInstancesRequest, DescribeInstancesRequest}
//import com.amazonaws.Request
//import com.amazonaws.services.ec2.model.transform.{DescribeInstancesRequestMarshaller, DescribeReservedInstancesRequestMarshaller}
//import java.net.URI
//import java.util.Map.Entry
//import com.amazonaws.auth.{QueryStringSigner, PropertiesCredentials}
//
//import scala.collection.JavaConversions._
//import org.apache.http.HttpRequestFactory
//
//object Experiments {
//  def main(args: Array[String]) {
////    val s3 = S3.create(new File("AwsCredentials.properties"))
////    s3.s3.doesBucketExist()
//
//    val ec2 = EC2.create(new File("AwsCredentials.properties"))
//    //val req =ec2.getInstanceById("id")
//    val req = new DescribeInstancesRequest().withInstanceIds("i-84a964c9")
//    val request: Request[DescribeInstancesRequest] = new DescribeInstancesRequestMarshaller().marshall(req)
//
//
//
//
//    val endpoint = new URI("http://ec2.eu-west-1.amazonaws.com")
//    request.setEndpoint(endpoint)
//
//    val params: Map[String, String] = request.getOriginalRequest.copyPrivateRequestParameters().toMap
//
//    for(entry <- params) {
//      request.addParameter(entry._1, entry._2)
//    }
//
//
//    val credentials = new PropertiesCredentials(new File("AwsCredentials.properties"))
////    AWSCredentials credentials = awsCredentialsProvider.getCredentials();
//
//
//    val originalRequest = request.getOriginalRequest()
////    if (originalRequest != null && originalRequest.getRequestCredentials() != null) {
////      credentials = originalRequest.getRequestCredentials();
////    }
////
////    ExecutionContext executionContext = createExecutionContext();
// //    executionContext.setSigner(signer);
////    executionContext.setCredentials(credentials);
////
////    StaxResponseHandler<X> responseHandler = new StaxResponseHandler<X>(unmarshaller);
////    DefaultErrorResponseHandler errorResponseHandler = new DefaultErrorResponseHandler(exceptionUnmarshallers);
////
////    return (X)client.execute(request, responseHandler, errorResponseHandler, executionContext);
//   // println(req.)
//
//    println(request.getHeaders)
//    val signer = new QueryStringSigner()
//    signer.sign(request, credentials)
//    println(request.getParameters)
//    println(request.getContent)
//    println(request.getResourcePath)
//    //val httpRequestFactory = new HttpRequestFactory()
//
//   // httpRequest = httpRequestFactory.createHttpRequest(request, config, entity, executionContext)
//
//  }
//
//}
