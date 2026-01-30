package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.model.hub.DeviceRemoved;
import ru.yandex.practicum.model.hub.base.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;
import ru.yandex.practicum.service.KafkaEventProducer;
import ru.yandex.practicum.service.handler.hub.base.BaseHubEventHandler;

@Component
public class DeviceRemovedHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {

    public DeviceRemovedHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED_EVENT;
    }

    @Override
    public DeviceRemovedEventAvro mapToAvro(HubEvent hubEvent) {
        DeviceRemoved deviceRemoved = (DeviceRemoved) hubEvent;
        return DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemoved.getId())
                .build();
    }
}
