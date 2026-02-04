package ru.yandex.practicum.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaTopicConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.AggregationService;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    @Value("${server.kafka.consumer.poll-timeout-ms:5000}")
    public int pollDurationMillis;

    private final AggregationService aggregationService;

    private final KafkaConsumer<String, SensorEventAvro> consumer;

    private final KafkaProducer<String, SpecificRecordBase> producer;

    private final KafkaTopicConfig topicConfig;

    public void start() {
        try {
            consumer.subscribe(List.of(topicConfig.getSensors()));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(
                        Duration.ofMillis(pollDurationMillis)
                );

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    handleRecord(record);
                }

                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
            log.warn("AggregationStarter got stop signal. Stopping aggregationStarter");
        } catch (Exception e) {
            log.error("Error in AggregationStarter during handling records", e);
        } finally {

            try {
                producer.flush();
                consumer.commitAsync();

            } finally {
                log.info("Closing consumer");
                consumer.close();
                log.info("Closing producer");
                producer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, SensorEventAvro> record) {
        log.trace("handled Record: topic {}, partition {}, offset {}, value {}",
                record.topic(), record.partition(), record.offset(), record.value());
        Optional<SensorsSnapshotAvro> sensorsSnapshotAvro = aggregationService.updateState(record.value());

        sensorsSnapshotAvro.ifPresent(snapshotAvro -> {
            log.info("sending updated snapshot for hubId: {}", snapshotAvro.getHubId());
            producer.send(new ProducerRecord<>(topicConfig.getSnapshots(), snapshotAvro));
        });
    }
}