package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dao.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SensorMapper {

    @Mapping(target = "hubId", source = "hubId")
    Sensor toSensor(String hubId, DeviceAddedEventAvro device);
}
