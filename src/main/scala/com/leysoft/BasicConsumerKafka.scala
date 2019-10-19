package com.leysoft

import java.time.{Duration => JDuration}
import java.util
import java.util.Properties

import akka.actor.ActorSystem

import scala.jdk.CollectionConverters._
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer

import scala.util.{Failure, Success, Try}

object BasicConsumerKafka extends App {
  implicit val system = ActorSystem("consumer-system")
  val bootstrapServers = "localhost:9092"
  val topic = "akka.topic"
  val `groupId` = "akka.group"
  val `autoCommit` = "false"
  val `autoOffset` = "earliest"

  val consumerSettings = new Properties
  consumerSettings.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
  consumerSettings.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
    classOf[StringDeserializer].getCanonicalName)
  consumerSettings.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    classOf[EventDeserializer].getCanonicalName)
  consumerSettings.put(ConsumerConfig.GROUP_ID_CONFIG, `groupId`)
  consumerSettings.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, `autoCommit`)
  consumerSettings.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, `autoOffset`)

  val timeOut = JDuration.ofMillis(10)
  val kafkaConsumer = new KafkaConsumer[String, Event](consumerSettings)

  kafkaConsumer.subscribe(List(topic).asJava)
  while (true) {
    Try(kafkaConsumer.poll(timeOut)) match {
      case Success(value) => value.asScala.foreach { record =>
        system.log.info(s"Consumer event: ${record.value}")
        kafkaConsumer.commitAsync((offsets: util.Map[TopicPartition, OffsetAndMetadata], exception: Exception) =>
          system.log.info("Commit...")
        )
      }
      case Failure(exception) => system.log.error(s"Error: $exception")
    }
  }
}
