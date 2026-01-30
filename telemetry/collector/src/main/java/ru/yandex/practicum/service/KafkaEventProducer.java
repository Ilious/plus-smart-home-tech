package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.config.KafkaTopicConfig;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProducer implements AutoCloseable {

    private final KafkaTopicConfig topicConfig;

    private final Producer<String, SpecificRecordBase> producer;

    public void send(String topic, String key, Instant timestamp, SpecificRecordBase data) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                timestamp.toEpochMilli(),
                key,
                data);

        producer.send(record, (metadata, e) -> {
            if (e != null)
                log.warn("Failed to send message to topic {}", topic, e);
            else
                log.info("Successfully sent message to topic {} offset {}", metadata.topic(), metadata.offset());
        });
    }

    public void sendSensorEvent(SpecificRecordBase data, String key, Instant timestamp) {
        send(topicConfig.getSensors(), key, timestamp, data);
    }

    public void sendHubEvent(SpecificRecordBase data, String key, Instant timestamp) {
        send(topicConfig.getHubs(), key, timestamp, data);
    }

    @Override
    public void close() {
        producer.flush();
        producer.close(Duration.ofSeconds(5));
    }
}
