package com.leysoft

import akka.actor.ActorSystem
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import com.leysoft.Parameters._
import com.leysoft.Parameters.ProducerParameters._

import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

object PublisherKafka extends App {
  implicit val system = ActorSystem("publisher-system")
  implicit val materializer = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new StringSerializer(), EventSerializer())
    .withBootstrapServers(bootstrapServers)
    .withProperty(ProducerConfig.CLIENT_ID_CONFIG, `clientId`)
    .withProperty(ProducerConfig.BATCH_SIZE_CONFIG, `batchSize`)
    .withProperty(ProducerConfig.LINGER_MS_CONFIG, `lingerMilliseconds`)
    .withProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, `compression`)
    .withProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, `idempotence`)
    .withProperty(ProducerConfig.ACKS_CONFIG, `acks`)
    .withProperty(ProducerConfig.RETRIES_CONFIG, `retries`)
  val kafkaProducer = producerSettings.createKafkaProducer()

  /*
  Source(List("Hello", "Akka", "Alpakka", "Kafka"))
    .map { value => new ProducerRecord[String, Event](topic, Event(value)) }
    .runWith(Producer.plainSink(producerSettings, kafkaProducer))*/

  Source(List("Hello", "Akka", "Alpakka", "Kafka"))
    .map { value =>
      ProducerMessage.single(new ProducerRecord[String, Event](topic, Event(value)))
    }.via(Producer.flexiFlow(producerSettings)).runWith(Sink.head)
}
