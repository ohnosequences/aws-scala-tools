package ohnosequences.awstools.utils

import com.amazonaws.AmazonClientException
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.{DeleteItemRequest, ProvisionedThroughputExceededException, AttributeValue, GetItemRequest}
import ohnosequences.logging.Logger

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions._

object DynamoDBUtils {


  //with repeats
  def getItem(ddb: AmazonDynamoDB,
              tableName: String,
              key: Map[String, AttributeValue],
              attributesToGet: Seq[String],
              logger: Logger): Try[Map[String, AttributeValue]] = {

    @tailrec
    def getItemRec(): Try[Map[String, AttributeValue]] = {
      try {
        val rawItem = ddb.getItem(new GetItemRequest()
          .withTableName(tableName)
          .withKey(key)
          .withAttributesToGet(attributesToGet)
        ).getItem
        if (rawItem != null) {
          Success(rawItem.toMap)
        } else {
          Failure(new NullPointerException)
        }
      } catch {
        case p: ProvisionedThroughputExceededException => {
          getItemRec()
        }
        case a: AmazonClientException => {
          Failure(a)
        }
      }
    }

    getItemRec()
  }

  //with repeats
  def deleteItem(ddb: AmazonDynamoDB,
                 tableName: String,
                 key: Map[String, AttributeValue],
                 logger: Logger): Try[Unit] = {

    @tailrec
    def deleteItemRep(): Try[Unit] = {
      try {
        val rawItem = ddb.deleteItem(new DeleteItemRequest()
          .withTableName(tableName)
          .withKey(key)
        )
        Success(())
      } catch {
        case p: ProvisionedThroughputExceededException => {
          deleteItemRep()
        }
        case a: AmazonClientException => {
          Failure(a)
        }
      }
    }

    deleteItemRep()
  }

}
