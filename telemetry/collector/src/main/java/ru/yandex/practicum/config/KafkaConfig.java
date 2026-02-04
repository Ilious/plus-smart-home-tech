package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

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
