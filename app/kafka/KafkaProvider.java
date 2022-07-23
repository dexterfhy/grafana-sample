package kafka;

import com.google.inject.Inject;
import cyclops.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.time.Duration;

import static config.Env.KAFKA_CONSUMER_ENABLED;
import static config.Env.KAFKA_CONSUMER_POLL_TIMEOUT_MS;
import static config.Env.KAFKA_DEFAULT_TOPIC;
import static config.KafkaConfiguration.getConsumerConfig;
import static config.KafkaConfiguration.getProducerConfig;
import static java.util.Collections.singletonList;

@Singleton
public class KafkaProvider {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProvider.class);
    private final KafkaProducer<String, String> producer;
    private final KafkaConsumer<String, String> consumer;

    @Inject
    public KafkaProvider() {
        this.producer = new KafkaProducer<>(getProducerConfig());
        this.consumer = new KafkaConsumer<>(getConsumerConfig());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.wakeup();
            Try.runWithCatch(() -> Thread.currentThread().join());
        }));

        if (KAFKA_CONSUMER_ENABLED.getBoolValue()) {
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
    }

    public void send(String message) {
        producer.send(new ProducerRecord<>(KAFKA_DEFAULT_TOPIC.getValue(), message));
    }

    private static void processRecord(ConsumerRecord<String, String> record) {
        logger.info(record.value());
    }

}
