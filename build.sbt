name := "akka-kafka"

version := "0.1"

scalaVersion := "2.13.1"

val akkaParent = "com.typesafe.akka"
val akkaVersion = "2.5.23"
val kafkaAlpakkaVersion = "1.1.0"
val kafkaParent = "org.apache.kafka"
val kafkaVersion = "2.3.0"
val sprayJsonParent = "io.spray"
val sprayJsonVersion = "1.3.5"
val scalaLoggingParent = "com.typesafe.scala-logging"
val scalaLoggingVersion = "3.9.2"
val logbackParent = "ch.qos.logback"
val logbackVersion = "1.2.3"
val scalaTestParent = "org.scalatest"
val scalaTestVersion = "3.0.8"

libraryDependencies ++= Seq(
  akkaParent %% "akka-stream" % akkaVersion,
  scalaLoggingParent %% "scala-logging" % scalaLoggingVersion,
  logbackParent % "logback-classic" % logbackVersion,
  akkaParent %% "akka-stream-kafka" % kafkaAlpakkaVersion,
  sprayJsonParent %%  "spray-json" % sprayJsonVersion,
  kafkaParent % "kafka-clients" % kafkaVersion, // for Kafka
  //kafkaParent % "kafka-streams" % kafkaVersion, // for Kafka kafka-streams

  scalaTestParent %% "scalatest" % scalaTestVersion % Test,
  akkaParent %% "akka-stream-testkit" % akkaVersion % Test,
  akkaParent %% "akka-testkit" % akkaVersion % Test
)
