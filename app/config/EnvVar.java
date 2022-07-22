package config;

import cyclops.control.Try;
import lombok.AllArgsConstructor;

import java.util.Optional;

import static java.util.function.Predicate.not;

@AllArgsConstructor
public enum EnvVar {

    KAFKA_BOOTSTRAP_SERVERS("localhost:9092"),
    KAFKA_GROUP_ID("default-group"),
    KAFKA_DEFAULT_TOPIC("grafana-test-topic"),
    KAFKA_CONSUMER_POLL_TIMEOUT_MS("1000");

    private final String defaultValue;

    public String getValue() {
        return Optional.ofNullable(System.getenv(this.name()))
            .filter(not(String::isBlank))
            .orElse(defaultValue);
    }

    public Optional<Long> getNumValue() {
        return Try.withCatch(() -> Long.parseLong(getValue())).toOptional();
    }

}
