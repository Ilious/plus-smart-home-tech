package ru.yandex.practicum.model.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensor.base.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

@Getter @Setter
@ToString(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {

    private int temperatureC;

    private int temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
