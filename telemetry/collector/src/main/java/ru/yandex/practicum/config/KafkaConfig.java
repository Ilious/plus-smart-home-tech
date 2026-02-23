package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaConfigData kafkaConfigData;

    @Bean
    public KafkaProducer<String, SpecificRecordBase> getProducer() {
        KafkaConfigData.Producer producerConfig = kafkaConfigData.getProducer();
        return new KafkaProducer<>(producerConfig.getProperties());
    }
}
