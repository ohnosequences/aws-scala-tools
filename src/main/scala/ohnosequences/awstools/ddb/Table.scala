package ohnosequences.awstools.ddb

import scala.collection.mutable
import com.amazonaws.services.dynamodbv2.model._
import scala.collection.JavaConversions._


class Table[AS <: Attributes](ddb: DynamoDB, name: String, val attributes: AS) {

  class ItemBuilder() {
    val map = new java.util.HashMap[String, AttributeValue]()
    map.put("constant", new AttributeValue().withN("1"))

    def withV[R](attr: attributes.Attr[R], v: R): ItemBuilder = {
      map.put(attr.name, attr.attrType.write(v))
      this
    }

    def put() {
      ddb.ddb.putItem(new PutItemRequest()
        .withTableName(name)
        .withItem(map)
      )
    }
  }

  def put(item: Map[attributes.AttrAux, String]) {
    val res = new java.util.HashMap[String, AttributeValue]()
    item.foreach{ case (attr, v) =>
      res.put(attr.name, attr.attrType.write(attr.attrType.fromString(v)))
    }
    res.put("constant", new AttributeValue().withN("1"))
    ddb.ddb.putItem(new PutItemRequest()
      .withTableName(name)
      .withItem(res)
    )
  }

  def put(item: Map[attributes.AttrAux, Any]) {
    ddb.ddb.batchWriteItem(new BatchWriteItemRequest()
      .withRequestItems()
    )
    val res = new java.util.HashMap[String, AttributeValue]()
    item.foreach{ case (attr, v) =>
      res.put(attr.name, attr.attrType.write(attr.attrType.fromString(v)))
    }
    res.put("constant", new AttributeValue().withN("1"))
    ddb.ddb.putItem(new PutItemRequest()
      .withTableName(name)
      .withItem(res)
    )
  }

  def put() = new ItemBuilder()

  private def get[R, S](key: attributes.Attr[R], v: R): Map[String, AttributeValue] = {
    ddb.ddb.getItem(new GetItemRequest()
      .withTableName(name)
      .withKey(Map(
      "constant" -> new AttributeValue().withN("1"),
      key.name -> key.attrType.write(v)
    ))
    ).getItem.toMap
  }


  def get[R, S](key: attributes.Attr[R], v: R, attr: attributes.Attr[S]): S = {
    val res = get(key, v)
    attr.attrType.read(res(attr.name))
  }

  def get[R, S1, S2](key: attributes.Attr[R], v: R, attr1: attributes.Attr[S1], attr2: attributes.Attr[S2]): (S1, S2) = {
    val res = get(key, v)
    (attr1.attrType.read(res(attr1.name)), attr2.attrType.read(res(attr2.name)))
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
