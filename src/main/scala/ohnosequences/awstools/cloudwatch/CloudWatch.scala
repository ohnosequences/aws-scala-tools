package ohnosequences.awstools.cloudwatch

import java.io.File

import com.amazonaws.auth.{BasicAWSCredentials, AWSCredentials, PropertiesCredentials}
import com.amazonaws.services.cloudwatch.{model, AmazonCloudWatchClient, AmazonCloudWatch}
import com.amazonaws.services.cloudwatch.model.{GetMetricStatisticsRequest, StandardUnit, MetricDatum, PutMetricDataRequest}

class CloudWatch(val cw: AmazonCloudWatch) {

  def shutdown() {
    cw.shutdown()
  }

  def putMetricData(metric: String, namespace: String, value: Double) {
    cw.putMetricData(
      new PutMetricDataRequest()
        .withNamespace(namespace)
        .withMetricData(
          new MetricDatum()
            .withMetricName(metric)
            .withUnit(StandardUnit.Count)
            .withValue(value)
        )
    )
  }

  def getStats(metric: String) = {
    cw.getMetricStatistics(
      new GetMetricStatisticsRequest()
        .withMetricName(metric)
    )
  }



}

object CloudWatch {


  def create(credentialsFile: File): CloudWatch = {
    create(new PropertiesCredentials(credentialsFile))
  }

  def create(accessKey: String, secretKey: String): CloudWatch = {
    create(new BasicAWSCredentials(accessKey, secretKey))
  }

  def create(credentials: AWSCredentials): CloudWatch = {
    val cwClient = new AmazonCloudWatchClient(credentials)
    cwClient.setEndpoint("http://monitoring.eu-west-1.amazonaws.com")
    new CloudWatch(cwClient)
  }
}
