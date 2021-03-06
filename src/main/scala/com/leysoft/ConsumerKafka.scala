package com.leysoft

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

import Parameters._
import Parameters.ConsumerParameters._

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object ConsumerKafka extends App {
  implicit val system = ActorSystem("consumer-system")
  implicit val materializer = ActorMaterializer()
  implicit val executor = ExecutionContext.global

  val subscription = Subscriptions.topics(topic)

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, EventDeserializer())
    .withBootstrapServers(bootstrapServers)
    .withGroupId(`groupId`)
    .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, `autoCommit`)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, `autoOffset`)
    .withProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, `insolation`)
  val kafkaConsumer = consumerSettings.createKafkaConsumer()
  val committerSettings = CommitterSettings(system)

  val process = (message: CommittableMessage[String, Event]) => {
    system.log.info(s"Consumer event: ${message.record.value}")
    message
  }

  val commit = (message: CommittableMessage[String, Event]) => {
    system.log.info(s"Commit $message...")
    Future(message.committableOffset)
  }

  Consumer.committableSource[String, Event](consumerSettings, subscription)
    .map { process }
    .mapAsync(5) { commit }
    .runWith(Sink.foreach { event => system.log.info(s"Sink: $event") })

  /*
  Consumer.plainSource[String, Event](consumerSettings, subscription)
    .runWith(Sink.foreach { event => println(s"Sink: $event") })*/
}
