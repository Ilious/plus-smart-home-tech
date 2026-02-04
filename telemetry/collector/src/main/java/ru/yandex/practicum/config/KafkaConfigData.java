package ru.yandex.practicum.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter @Setter @ToString
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaConfigData {

    private Producer producer;

    @Getter @Setter @ToString
    public static class Producer {
        private Properties properties;
    }
}
