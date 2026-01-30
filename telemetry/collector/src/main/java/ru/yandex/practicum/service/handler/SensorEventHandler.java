package ru.yandex.practicum.service.handler;

import ru.yandex.practicum.model.sensor.base.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

public interface SensorEventHandler {

    SensorEventType getMessageType();

    void handle(SensorEvent event);
}
