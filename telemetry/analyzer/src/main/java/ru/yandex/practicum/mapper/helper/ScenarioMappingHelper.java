package ru.yandex.practicum.mapper.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dao.Action;
import ru.yandex.practicum.dao.Condition;
import ru.yandex.practicum.dao.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.ScenarioMapper;
import ru.yandex.practicum.service.SensorService;

import java.util.*;
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
        if (actions == null || actions.isEmpty())
            return new HashMap<>();

        Set<String> validIds = getValidIds(
                hubId,
                actions.stream().map(DeviceActionAvro::getSensorId).toList(),
                "mapActions"
        );

        return actions.stream()
                .filter(action -> validIds.contains(action.getSensorId()))
                .collect(Collectors.toMap(
                        DeviceActionAvro::getSensorId,
                        scenarioMapper::toAction
                ));
    }

    public Map<String, Condition> mapConditions(String hubId, List<ScenarioConditionAvro> conditions) {
        if (conditions == null || conditions.isEmpty())
            return new HashMap<>();

        Set<String> validIds = getValidIds(
                hubId,
                conditions.stream().map(ScenarioConditionAvro::getSensorId).toList(),
                "mapConditions"
        );

        return conditions.stream()
                .filter(condition -> validIds.contains(condition.getSensorId()))
                .collect(Collectors.toMap(
                        ScenarioConditionAvro::getSensorId,
                        scenarioMapper::toCondition
                ));
    }

    private Set<String> getValidIds(String hubId, List<String> requestedIds, String methodName) {
        if (requestedIds.isEmpty()) return new HashSet<>();

        List<String> uniqueIds = requestedIds.stream()
                .distinct()
                .toList();
        Set<String> existingIds = new HashSet<>(sensorService.findExistingIdsByHubIdAndIdIn(hubId, uniqueIds)); // o(1) lookup

       uniqueIds.stream()
                .map(id -> !existingIds.contains(id))
                .forEach(id -> log.warn("{} can't find sensor: hubId={}, sensorId={}", methodName, hubId, id));
       return existingIds;
    }
}