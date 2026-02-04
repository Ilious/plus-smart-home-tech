package ru.yandex.practicum.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaTopicConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.HubEventClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    @Value("${server.kafka.consumer.poll-timeout-ms:5000}")
    public int pollDurationMillis;

    @Value("${server.kafka.hubEventProcessor.batch-messages-count:10}")
    public int batchMessagesCount;

    private final KafkaTopicConfig kafkaTopicConfig;

    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;

    private final KafkaProducer<String, SpecificRecordBase> producer;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final HubEventClient hubEventClient;

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(kafkaTopicConfig.getSensors()));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(pollDurationMillis));

                int count = 0;
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    handleRecord(record);

                    manageOffset(record, count);

                    count++;
                }

                consumer.commitAsync();
            }
        } catch (WakeupException e) {
            log.warn("HubEventProcessor got stop signal. Stopping aggregationStarter");
        } catch (Exception e) {
            log.error("Error in HubEventProcessor during handling records", e);
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

    private void manageOffset(ConsumerRecord<String, SensorsSnapshotAvro> record, int count) {
        log.trace("[HubEventProcessor] - Received record: {}, topic {}, partition {}, offset {}", record.value(), record.topic(), record.partition(), record.offset());
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % batchMessagesCount == 0) {
            consumer.commitAsync(currentOffsets, (offset, exception) -> {
                if (exception != null) {
                    log.warn("[HubEventProcessor] - Error during fixing offsets: {}", currentOffsets, exception);
                }
            });
        }
    }

    private void handleRecord(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        log.trace("[HubEventProcessor] - handled Record: topic {}, partition {}, offset {}, value {}",
                record.topic(), record.partition(), record.offset(), record.value());

        Map<String, SensorStateAvro> sensorsState = record.value().getSensorsState();

        // Todo
//        sensorsState.get()
//
//        hubEventClient.handleDeviceAction(record.value().);
    }
}
