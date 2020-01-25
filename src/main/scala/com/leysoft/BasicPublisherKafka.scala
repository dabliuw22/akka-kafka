package com.leysoft

import java.util.Properties

import akka.actor.ActorSystem

import Parameters._
import Parameters.ProducerParameters._

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer

object BasicPublisherKafka extends App {
  implicit val system = ActorSystem("consumer-system")

  val producerSettings = new Properties
  producerSettings.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
  producerSettings.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    classOf[StringSerializer].getCanonicalName)
  producerSettings.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    classOf[EventSerializer].getCanonicalName)
  producerSettings.put(ProducerConfig.CLIENT_ID_CONFIG, `clientId`)
  producerSettings.put(ProducerConfig.BATCH_SIZE_CONFIG, `batchSize`)
  producerSettings.put(ProducerConfig.LINGER_MS_CONFIG, `lingerMilliseconds`)
  producerSettings.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, `compression`)
  producerSettings.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, `idempotence`)
  producerSettings.put(ProducerConfig.RETRIES_CONFIG, `retries`)
  producerSettings.put(ProducerConfig.ACKS_CONFIG, `acks`)

  lazy val kafkaProducer = new KafkaProducer[String, Event](producerSettings)
  List("Hi", "JVM", "Kafka Client")
    .map { value => Event(value) }
    .map { event => new ProducerRecord[String, Event](topic, event) }
    .foreach { record =>
      system.log.info(s"Send: $record")
      //kafkaProducer.send(record) // for ack sync
      kafkaProducer.send(record, (metadata: RecordMetadata, error: Exception) =>
          system.log.info(s"Async, metadata: $metadata, error: $error")) // for ack async
    }
}
