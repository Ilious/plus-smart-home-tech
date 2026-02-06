package ru.yandex.practicum.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class BaseProcessor<T extends SpecificRecordBase> {

    @Value("${server.kafka.consumer.poll-timeout-ms:5000}")
    protected int pollDurationMillis;

    @Value("${server.kafka.consumer.batch-messages-count:10}")
    protected int batchMessagesCount;

    protected final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    protected abstract void handleRecord(ConsumerRecord<String, T> record);

    protected void manageOffset(ConsumerRecord<String, T> record, int count, KafkaConsumer<String, T> consumer) {
        log.trace("Received record: {}, topic {}, partition {}, offset {}",
                record.value(), record.topic(), record.partition(), record.offset());

        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % batchMessagesCount == 0) {
            consumer.commitAsync(currentOffsets, (offset, exception) -> {
                if (exception != null) {
                    log.warn("Error during fixing offsets: {}", currentOffsets, exception);
                }
            });
        }
    }
}
