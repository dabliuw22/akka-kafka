package com.leysoft

object Parameters {

  val bootstrapServers = "localhost:9092"
  val topic = "akka.topic"

  object ProducerParameters {
    val `clientId` = "akka.client"
    val `acks` = "all"
    val `retries` = "3"
    val `compression` = "snappy"
    val `idempotence` = "true"
    val `batchSize` = "500"
    val `lingerMilliseconds` = "10"
  }

  object ConsumerParameters {
    val `groupId` = "akka.group"
    val `autoCommit` = "false"
    val `autoOffset` = "earliest"
    val `insolation` = "read_committed"
  }
}
