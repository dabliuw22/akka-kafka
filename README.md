# Akka Kafka

1. Dowload Apache Kafka.

2. Run Apache Zookeeper:
    `./{KAFKA_PATH}/bin/zookeeper-server-start.sh ./config/zookeeper.properties`

3. Run Apache Kafka:
    `./{KAFKA_PATH}/bin/kafka-server-start.sh ./config/server.properties`
    
4. Create `akka.topic`:
    * With a Single partions:
        `./{KAFKA_PATH}/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic akka.topic`
    * With multiple partitions:
        `./{KAFKA_PATH}/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic akka.topic`
    
5. Run Consumer App:
    * `ConsumerKafka`: Akka Streams Kafka.
    * `BasicConsumerKafka`: Kafka Client Api.

6. Run Publisher App:
    * `PublisherKafka`: Akka Streams Kafka.
    * `BasicPublisherKafka`: Kafka Client Api.