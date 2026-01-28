package ru.yandex.practicum.model.sensor;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensor.base.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

@Getter @Setter
@ToString(callSuper = true)
public class LightSensorEvent extends SensorEvent {

    private int linkQuality;

    private int luminosity;

    @Override
    public SensorEventType getEventType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
