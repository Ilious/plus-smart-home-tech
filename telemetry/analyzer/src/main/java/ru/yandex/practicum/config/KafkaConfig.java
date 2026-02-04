package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaConfigData kafkaConfigData;

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> getConsumer() {
        KafkaConfigData.Consumer consumerConfig = kafkaConfigData.getConsumer();
        return new KafkaConsumer<>(consumerConfig.getProperties());
    }

    @Bean
    public KafkaProducer<String, SpecificRecordBase> getProducer() {
        KafkaConfigData.Producer producerConfig = kafkaConfigData.getProducer();
        return new KafkaProducer<>(producerConfig.getProperties());
    }
}
