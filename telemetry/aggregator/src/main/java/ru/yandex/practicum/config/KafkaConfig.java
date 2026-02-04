package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaConfigData kafkaConfigData;

    @Bean
    public KafkaConsumer<String, SensorEventAvro> getConsumer() {
        KafkaConfigData.Consumer consumerConfig = kafkaConfigData.getConsumer();
        return new KafkaConsumer<>(consumerConfig.getProperties());
    }

    @Bean
    public KafkaProducer<String, SpecificRecordBase> getProducerAvro() {
        KafkaConfigData.Producer producerConfig = kafkaConfigData.getProducer();
        return new KafkaProducer<>(producerConfig.getProperties());
    }
}