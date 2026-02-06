package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter @Setter @ToString
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaConfigData {

    private Producer producer;

    private Consumer consumer;

    @Getter @Setter @ToString
    public static class Producer {
        private Properties properties;
    }

    @Getter @Setter @ToString
    public static class Consumer {
        private Properties properties;
    }
}