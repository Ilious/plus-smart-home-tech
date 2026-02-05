package ru.yandex.practicum.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaTopicConfig;
import ru.yandex.practicum.dal.dao.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.AnalyzerService;
import ru.yandex.practicum.service.HubEventClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor extends BaseProcessor<SensorsSnapshotAvro> {

    private final KafkaTopicConfig topicConfig;

    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;

    private final KafkaProducer<String, SpecificRecordBase> producer;

    private final AnalyzerService analyzerService;

    private final HubEventClient hubEventClient;

    public void start() {
        try {
            consumer.subscribe(List.of(topicConfig.getSnapshots()));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(
                        Duration.ofMillis(pollDurationMillis)
                );

                int count = 0;
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    handleRecord(record);

                    manageOffset(record, count, consumer);

                    count++;
                }

                consumer.commitAsync();
            }

        } catch (WakeupException e) {
            log.warn("Got stop signal. Stopping aggregationStarter");
        } catch (Exception e) {
            log.error("Error during handling records", e);
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

    @Override
    public void handleRecord(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        log.trace("Handled Record: topic {}, partition {}, offset {}, value {}",
                record.topic(), record.partition(), record.offset(), record.value());

        List<Scenario> completeScenarios = analyzerService.analyze(record.value());
        completeScenarios.forEach(hubEventClient::handleScenario);
    }
}