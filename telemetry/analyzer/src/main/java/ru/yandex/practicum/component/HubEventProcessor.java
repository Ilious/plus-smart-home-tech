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
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.ScenarioService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor extends BaseProcessor<HubEventAvro> implements Runnable {

    private final KafkaTopicConfig kafkaTopicConfig;

    private final KafkaConsumer<String, HubEventAvro> consumer;

    private final KafkaProducer<String, SpecificRecordBase> producer;

    private final ScenarioService scenarioService;

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(kafkaTopicConfig.getHubs()));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(
                        Duration.ofMillis(pollDurationMillis)
                );

                int count = 0;
                for (ConsumerRecord<String, HubEventAvro> record : records) {
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
    public void handleRecord(ConsumerRecord<String, HubEventAvro> record) {
        log.trace("handled Record: topic {}, partition {}, offset {}, value {}",
                record.topic(), record.partition(), record.offset(), record.value());

        Object payload = record.value().getPayload();
        String hubId = record.value().getHubId();

        switch (payload) {
            case DeviceAddedEventAvro deviceAddedEvent ->
                    scenarioService.addDevice(hubId, deviceAddedEvent);
            case DeviceRemovedEventAvro deviceRemovedEventAvro ->
                    scenarioService.removeDevice(hubId, deviceRemovedEventAvro);
            case ScenarioAddedEventAvro scenarioAddedEventAvro ->
                    scenarioService.addScenario(hubId, scenarioAddedEventAvro);
            case ScenarioRemovedEventAvro scenarioRemovedEventAvro ->
                    scenarioService.removeScenario(hubId, scenarioRemovedEventAvro);
            default -> log.warn("unhandled unknown Scenario: hubId {}",
                    record.value().getHubId()
            );
        }
    }
}
