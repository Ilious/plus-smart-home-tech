package ru.yandex.practicum.service.handler.sensor.base;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.base.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;
import ru.yandex.practicum.service.KafkaEventProducer;
import ru.yandex.practicum.service.handler.SensorEventHandler;

@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    private final KafkaEventProducer producer;

    public abstract SensorEventType getMessageType();

    protected abstract T mapToAvro(SensorEvent event);

    @Override
    public void handle(SensorEvent event) {
        if (!event.getType().equals(getMessageType())) {
            throw new IllegalArgumentException("Unknown sensor event type: " + event.getType());
        }

        T payload = mapToAvro(event);

        SensorEventAvro avroEvent = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setPayload(payload)
                .setTimestamp(event.getTimestamp())
                .build();

        producer.sendSensorEvent(avroEvent, event.getHubId(), event.getTimestamp());
    }
}
