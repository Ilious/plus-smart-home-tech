package ru.yandex.practicum.mapper.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dao.Action;
import ru.yandex.practicum.dao.Condition;
import ru.yandex.practicum.dao.Scenario;
import ru.yandex.practicum.dao.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.ScenarioMapper;
import ru.yandex.practicum.service.SensorService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioMappingHelper {

    private final SensorService sensorService;

    private final ScenarioMapper scenarioMapper;

    public Scenario buildScenario(String hubId, ScenarioAddedEventAvro event) {
        Scenario scenario = scenarioMapper.toScenario(hubId, event);
        scenario.setActions(mapActions(hubId, event.getActions()));
        scenario.setConditions(mapConditions(hubId, event.getConditions()));
        return scenario;
    }

    public Map<String, Action> mapActions(String hubId, List<DeviceActionAvro> actions) {
        return actions.stream()
                .filter(action -> validateSensor(hubId, action.getSensorId()))
                .collect(Collectors.toMap(
                        DeviceActionAvro::getSensorId,
                        scenarioMapper::toAction
                ));
    }

    public Map<String, Condition> mapConditions(String hubId, List<ScenarioConditionAvro> conditions) {
        return conditions.stream()
                .filter(condition -> validateSensor(hubId, condition.getSensorId()))
                .collect(Collectors.toMap(
                        ScenarioConditionAvro::getSensorId,
                        scenarioMapper::toCondition
                ));
    }

    private boolean validateSensor(String hubId, String sensorId) {
        Optional<Sensor> sensor = sensorService.findByIdAndHubId(sensorId, hubId);
        if (sensor.isEmpty()) {
            log.warn("Haven't found sensor: hubId={}, sensorId={}", hubId, sensorId);
            return false;
        }
        return true;
    }

}