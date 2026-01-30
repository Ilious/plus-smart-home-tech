package ru.yandex.practicum.model.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensor.base.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

@Getter @Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {

    private int temperatureC;

    private int humidity;

    private int co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
