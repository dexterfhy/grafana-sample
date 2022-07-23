package guice;

import com.google.inject.AbstractModule;
import kafka.KafkaProvider;

public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(KafkaProvider.class).asEagerSingleton();
    }

}