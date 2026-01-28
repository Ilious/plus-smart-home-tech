package ru.yandex.practicum.service.handler.hub;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.model.hub.DeviceAdded;
import ru.yandex.practicum.model.hub.base.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;
import ru.yandex.practicum.service.KafkaEventProducer;
import ru.yandex.practicum.service.handler.hub.base.BaseHubEventHandler;

@Component
public class DeviceAddedHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED_EVENT;
    }

    @Override
    public DeviceAddedEventAvro mapToAvro(HubEvent hubEvent) {
        DeviceAdded deviceAdded = (DeviceAdded) hubEvent;
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAdded.getId())
                .setType(mapToAvroDeviceType(deviceAdded))
                .build();
    }

    private DeviceTypeAvro mapToAvroDeviceType(DeviceAdded deviceAdded) {
        return DeviceTypeAvro.valueOf(deviceAdded.getDeviceType().name());
    }
}
