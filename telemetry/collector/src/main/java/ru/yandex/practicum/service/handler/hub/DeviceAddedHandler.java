package ru.yandex.practicum.service.handler.hub;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.service.KafkaEventProducer;
import ru.yandex.practicum.service.handler.hub.base.BaseHubEventHandler;

@Component
public class DeviceAddedHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public DeviceAddedEventAvro toMessage(HubEventProto hubEvent) {
        DeviceAddedEventProto deviceAdded = hubEvent.getDeviceAdded();
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAdded.getId())
                .setType(mapToAvroDeviceType(deviceAdded))
                .build();
    }

    private DeviceTypeAvro mapToAvroDeviceType(DeviceAddedEventProto deviceAdded) {
        return DeviceTypeAvro.valueOf(deviceAdded.getType().name());
    }
}
