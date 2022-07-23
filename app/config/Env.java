package config;

import cyclops.control.Try;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public enum Env {

    KAFKA_BOOTSTRAP_SERVERS("localhost:9092"),
    KAFKA_GROUP_ID("default-group"),
    KAFKA_DEFAULT_TOPIC("grafana-test-topic"),
    KAFKA_CONSUMER_POLL_TIMEOUT_MS("1000"),
    KAFKA_CONSUMER_ENABLED("true");

    private final String defaultValue;

    static final Dotenv DOTENV = Dotenv.load();

    public String getValue() {
        return Optional.ofNullable(DOTENV.get(this.name()))
            .or(() -> Optional.ofNullable(System.getenv(this.name())))
            .or(() -> Optional.ofNullable(System.getProperty(this.name())))
            .orElse(defaultValue);
    }

    public Optional<Long> getNumValue() {
        return Try.withCatch(() -> Long.parseLong(getValue())).toOptional();
    }

    public boolean getBoolValue() {
        return Boolean.parseBoolean(getValue());
    }

}
