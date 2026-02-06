package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.dao.Action;
import ru.yandex.practicum.dao.Condition;
import ru.yandex.practicum.dao.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScenarioMapper {

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "name", source = "event.name")
    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "conditions", ignore = true)
    Scenario toScenario(String hubId, ScenarioAddedEventAvro event);

    @Named("toAction")
    @Mapping(target = "value", source = "action.value", qualifiedByName = "mapValue")
    Action toAction(DeviceActionAvro action);

    @Named("toCondition")
    @Mapping(target = "value", source = "condition.value", qualifiedByName = "mapValue")
    Condition toCondition(ScenarioConditionAvro condition);

    @Named("mapValue")
    default Integer mapValue(Object value) {
        return switch (value) {
            case Integer i -> i;
            case Boolean b -> b ? 1 : 0;
            case null, default -> null;
        };
    }
}
