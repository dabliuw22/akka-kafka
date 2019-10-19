package com.leysoft

import java.util

import org.apache.kafka.common.serialization.{Deserializer, Serializer, StringDeserializer, StringSerializer}
import spray.json.DefaultJsonProtocol

case class Event(message: String)

trait EventJsonProtocol extends DefaultJsonProtocol {

  implicit val format = jsonFormat1(Event)
}

case class EventSerializer() extends EventJsonProtocol with Serializer[Event] {

  import spray.json._

  private val serializer = new StringSerializer

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
    serializer.configure(configs, isKey)

  override def serialize(topic: String, data: Event): Array[Byte] =
    serializer.serialize(topic, data.toJson.prettyPrint)

  override def close(): Unit = serializer.close()
}

case class EventDeserializer() extends EventJsonProtocol with Deserializer[Event] {

  import spray.json._

  private val deserializer = new StringDeserializer

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
    deserializer.configure(configs, isKey)

  override def deserialize(topic: String, data: Array[Byte]): Event =
    deserializer.deserialize(topic, data).parseJson.convertTo[Event]

  override def close(): Unit = deserializer.close()
}
