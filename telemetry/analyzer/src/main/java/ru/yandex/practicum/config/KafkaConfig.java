package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.deserializer.HubEventDeserializer;
import ru.yandex.practicum.deserializer.SensorsSnapshotDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaConfigData kafkaConfigData;

    private Properties getBaseConsumerProperties() {
        if (kafkaConfigData == null) {
            log.warn("consumer config data hasn't loaded yet");
            return new Properties();
        }

        return kafkaConfigData.getConsumer().getProperties();
    }

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> getSensorsSnapshotConsumer() {
        Properties properties = getBaseConsumerProperties();

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-snapshot-group");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
        return new KafkaConsumer<>(properties);
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> getHubEventConsumer() {
        Properties properties = getBaseConsumerProperties();

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-hub-event-group");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
        return new KafkaConsumer<>(properties);
    }

    @Bean
    public KafkaProducer<String, SpecificRecordBase> getProducer() {
        KafkaConfigData.Producer producerConfig = kafkaConfigData.getProducer();
        return new KafkaProducer<>(producerConfig.getProperties());
    }
}
