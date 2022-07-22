package kafka;

import cyclops.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static config.EnvVar.KAFKA_CONSUMER_POLL_TIMEOUT_MS;
import static config.EnvVar.KAFKA_DEFAULT_TOPIC;
import static config.KafkaConfiguration.getConsumerConfig;
import static config.KafkaConfiguration.getProducerConfig;
import static java.util.Collections.singletonList;

public enum SingletonKafkaProvider {

    KAFKA_MANAGER(new KafkaProducer<>(getProducerConfig()), new KafkaConsumer<>(getConsumerConfig()));

    private static final Logger logger = LoggerFactory.getLogger(SingletonKafkaProvider.class);
    private final KafkaProducer<String, String> producer;
    private final KafkaConsumer<String, String> consumer;

    SingletonKafkaProvider(KafkaProducer<String, String> producer, KafkaConsumer<String, String> consumer) {
        this.producer = producer;
        this.consumer = consumer;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.wakeup();
            Try.runWithCatch(() -> Thread.currentThread().join());
        }));

        Try.runWithCatch(() -> {
            consumer.subscribe(singletonList(KAFKA_DEFAULT_TOPIC.getValue()));

            new Thread(() -> {
                while (true) {
                    consumer
                        .poll(Duration.ofMillis(KAFKA_CONSUMER_POLL_TIMEOUT_MS.getNumValue().orElse(1000L)))
                        .forEach(r -> Try.runWithCatch(() -> processRecord(r)));
                }
            }).start();
        }).peekFailed(ex -> consumer.close());
    }

    public void send(String message) {
        producer.send(new ProducerRecord<>(KAFKA_DEFAULT_TOPIC.getValue(), message));
    }

    private static void processRecord(ConsumerRecord<String, String> record) {
        logger.info(record.value());
    }

}
