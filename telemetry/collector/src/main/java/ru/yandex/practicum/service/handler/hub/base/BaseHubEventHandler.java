package ru.yandex.practicum.service.handler.hub.base;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.base.HubEvent;
import ru.yandex.practicum.service.KafkaEventProducer;
import ru.yandex.practicum.service.handler.HubEventHandler;

@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    private final KafkaEventProducer producer;

    public abstract T mapToAvro(HubEvent hubEvent);

    @Override
    public void handle(HubEvent event) {
        if (!event.getType().equals(getMessageType())) {
            throw new IllegalArgumentException("Unknown hub event type: " + event.getType());
        }

        T payload = mapToAvro(event);

        HubEventAvro avroEvent = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setPayload(payload)
                .setTimestamp(event.getTimestamp())
                .build();

        producer.sendHubEvent(avroEvent, event.getHubId(), event.getTimestamp());
    }
}
