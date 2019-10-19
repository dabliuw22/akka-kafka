package com.leysoft

import java.util.Properties

import akka.actor.ActorSystem
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer

object BasicPublisherKafks extends App {
  implicit val system = ActorSystem("consumer-system")
  val bootstrapServers = "localhost:9092"
  val topic = "akka.topic"

  val producerSettings = new Properties
  producerSettings.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
  producerSettings.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    classOf[StringSerializer].getCanonicalName)
  producerSettings.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    classOf[EventSerializer].getCanonicalName)
  producerSettings.put(ProducerConfig.RETRIES_CONFIG, 3)
  producerSettings.put(ProducerConfig.ACKS_CONFIG, "all")

  lazy val kafkaProducer = new KafkaProducer[String, Event](producerSettings)
  List("Hi", "JVM", "Kafka Client")
    .map { value => Event(value) }
    .map { event => new ProducerRecord[String, Event](topic, event) }
    .foreach { record =>
      system.log.info(s"Send: $record")
      //kafkaProducer.send(record) // for sync
      kafkaProducer.send(record, (metadata: RecordMetadata, error: Exception) =>
          system.log.info(s"Async, metadata: $metadata, error: $error")) // for ack async
    }
}
