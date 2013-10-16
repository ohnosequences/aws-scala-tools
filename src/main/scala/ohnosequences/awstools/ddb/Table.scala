package ohnosequences.awstools.ddb

import scala.collection.mutable
import com.amazonaws.services.dynamodbv2.model._
import scala.collection.JavaConversions._
import java.util



class Table[AS <: Attributes](ddb: DynamoDB, name: String, val attributes: AS) {

  case class PutOperation(map: java.util.HashMap[String, AttributeValue]) {

    def putItemRequest: PutItemRequest = {
      new PutItemRequest()
        .withTableName(name)
        .withItem(map)
    }

    def putRequest: PutRequest = {
      new PutRequest()
        .withItem(map)
    }

    def writeRequest: WriteRequest = {
      new WriteRequest()
        .withPutRequest(putRequest)
    }

  }

  class PutOperationBuilder(hash: Int) {
    val map = new java.util.HashMap[String, AttributeValue]()
    map.put("hash", new AttributeValue().withN(hash.toString))

    def withV[R](attr: attributes.Attr[R], v: R): PutOperationBuilder = {
      map.put(attr.name, attr.attrType.write(v))
      this
    }

    def build(): PutOperation = {
      PutOperation(map)      
    }
  }

  def createPutOperation(hash: Int) = new PutOperationBuilder(hash)
  
  def put(putOperation: PutOperation) {
    ddb.ddb.putItem(putOperation.putItemRequest)
  }

  def putOps(writeOperations: java.util.ArrayList[WriteRequest]) {

    val map = new util.HashMap[String, java.util.List[WriteRequest]]()

    map.put(name, writeOperations)

    ddb.ddb.batchWriteItem(new BatchWriteItemRequest()
      .withRequestItems(map)
    )

  }

//  def put(putOperations: List[PutOperation]) {
//    putOps(putOperations.map(_.writeRequest))
//  }
}

class ParallelUploader[AS <: Attributes](table: Table[AS], workersCount: Int) {
  val batchSize = 1

  //val operationsQueue = new java.util.concurrent.ArrayBlockingQueue[Option[table.PutOperation]](workersCount * 25 * 2)
  val operationsQueue = new java.util.concurrent.ArrayBlockingQueue[Option[WriteRequest]](workersCount * batchSize * 2)

  class Worker(id: Int) extends Thread("upload_worker_" + id) {

    var stopped = false

    var buffer = new java.util.ArrayList[WriteRequest]()

    override def run() {
      println(getName + " runned")
      while(!stopped) {
        for(i <- 1 to batchSize) {
          operationsQueue.take() match {
            case None => stopped = true
            case Some(op) => buffer.add(op)
          }
        }

        if(!buffer.isEmpty) {
          println(getName + " publishing ")
          println(buffer)
          table.putOps(buffer)
          buffer.clear()
        }
      }
      println(getName + " stopped")
    }
  }

  def putToQueue(operation: WriteRequest) {
    operationsQueue.put(Some(operation))
  }

  def stopPutting() {
    for(i <- 1 to workersCount * batchSize * 2) {
      operationsQueue.put(None)
    }
  }

  val workers =  (for(i <- 1 to workersCount) yield new Worker(i)).toList

  def start() {
    workers.foreach(_.start())
  }
}



trait BaseAttr {
  type TT
  val name: String
  val attrType: AttrType[TT]
  val key: Boolean
}


trait Attributes {

  private val attributes = new mutable.HashSet[Attr[_]]()

  def getAttributes = attributes.toList

  type Attribute =  AttrAux

  trait AttrAux extends BaseAttr

  class Attr[R](val name: String, val attrType: AttrType[R], val key: Boolean = false) extends AttrAux {
    type TT = R
  }

  def attr[T](name: String, attrType: AttrType[T], key: Boolean = false) = {
    val a = new Attr[T](name, attrType, key)
    attributes.add(a)
    a
  }

  def getKeyDefinitions = attributes.filter(_.key).toList.map { attr =>
    new AttributeDefinition(attr.name, attr.attrType.scalarType)
  }

  def getKeySchema = attributes.filter(_.key).toList.map { attr =>
    new KeySchemaElement(attr.name, "RANGE")
  }
}


trait AttrTypeAux {
  type R
  def read(a: AttributeValue): R

  def fromString(s: String): R

  def write(v: R): AttributeValue

  val scalarType: ScalarAttributeType
}

trait AttrType[T] extends AttrTypeAux {
  type R = T
}

case object IntAttr extends AttrType[Int] {
  def read(a: AttributeValue): Int = a.getN.toInt

  def fromString(s: String) = s.toInt

  def write(v: Int): AttributeValue = new AttributeValue().withN(v.toString)

  val scalarType = ScalarAttributeType.N
}

case object StringAttr extends AttrType[String] {
  def read(a: AttributeValue): String = a.getS

  def fromString(s: String) = s

  def write(v: String): AttributeValue = new AttributeValue().withS(v)

  val scalarType = ScalarAttributeType.S
}
