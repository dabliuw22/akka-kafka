package com.leysoft

import akka.actor.ActorSystem
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

object PublisherKafka extends App {
  implicit val system = ActorSystem("publisher-system")
  implicit val materializer = ActorMaterializer()
  val bootstrapServers = "localhost:9092"
  val topic = "akka.topic"

  val producerSettings = ProducerSettings(system, new StringSerializer(), EventSerializer())
    .withBootstrapServers(bootstrapServers)
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
