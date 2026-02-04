package ru.yandex.practicum.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaTopicConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.AggregationService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    public static final int POLL_DURATION_MILLIS = 1000;

    private final AggregationService aggregationService;

    private final KafkaConsumer<String, SensorEventAvro> consumer;

    private final KafkaProducer<String, SpecificRecordBase> producer;

    private final KafkaTopicConfig topicConfig;

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        try {
            consumer.subscribe(List.of(topicConfig.getSensors()));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Got stop signal. Stopping aggregationStarter");
                consumer.wakeup();
            }));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(
                        Duration.ofMillis(POLL_DURATION_MILLIS)
                );

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    handleRecord(record);
                }

                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                producer.flush();
                consumer.commitAsync();

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, SensorEventAvro> record) {
        log.debug("handled Record: topic {}, partition {}, offset {}, value {}",
                record.topic(), record.partition(), record.offset(), record.value());
        Optional<SensorsSnapshotAvro> sensorsSnapshotAvro = aggregationService.updateState(record.value());

        sensorsSnapshotAvro.ifPresent(snapshotAvro -> {
            log.info("sending updated snapshot for hubId: {}", snapshotAvro.getHubId());
            producer.send(new ProducerRecord<>(topicConfig.getSnapshots(), snapshotAvro));
        });
    }
}