package ru.yandex.practicum.service.handler.hub.base;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.model.hub.base.HubEvent;
import ru.yandex.practicum.service.KafkaEventProducer;
import ru.yandex.practicum.service.handler.HubEventHandler;

@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    private final KafkaEventProducer producer;

    public abstract T mapToAvro(HubEvent hubEvent);

    @Override
    public void handle(HubEvent event) {
        producer.sendHubEvent(mapToAvro(event), event.getHubId(), event.getTimestamp());
    }
}
