package com.leysoft

import akka.actor.ActorSystem
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

object ConsumerKafka extends App {
  implicit val system = ActorSystem("consumer-system")
  implicit val materializer = ActorMaterializer()
  val bootstrapServers = "localhost:9092"
  val topic = "akka.topic"
  val `groupId` = "akka.group"
  val `autoCommit` = "false"
  val `autoOffset` = "earliest"
  import spray.json._
  import spray.json.DefaultJsonProtocol._
  implicit val format = jsonFormat1(Event)

  val subscription = Subscriptions.topics(topic)

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer())
    .withBootstrapServers(bootstrapServers)
    .withGroupId(`groupId`)
    .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, `autoCommit`)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, `autoOffset`)
  val kafkaConsumer = consumerSettings.createKafkaConsumer()
  val committerSettings = CommitterSettings(system)

  Consumer.plainSource(consumerSettings, subscription)
    .map { event =>  event.value().parseJson.convertTo[Event]}
    .runWith(Sink.foreach{ event => println(s"Sink: $event") })
}
