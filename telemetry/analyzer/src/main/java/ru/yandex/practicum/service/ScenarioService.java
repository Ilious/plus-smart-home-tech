package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.Action;
import ru.yandex.practicum.dao.Condition;
import ru.yandex.practicum.dao.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.ScenarioMapper;
import ru.yandex.practicum.repository.ScenarioRepo;
import ru.yandex.practicum.utils.LogUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepo scenarioRepo;

    private final SensorService sensorService;

    @Transactional(readOnly = true)
    public List<Scenario> findAllByHubId(String hubId) {
        log.debug("findAllByHubId: hubId {}", hubId);

        return scenarioRepo.findAllByHubId(hubId);
    }

    @Transactional
    public void addDevice(String hubId, DeviceAddedEventAvro event) {
        log.debug("AddDevice: hubId {}, id {}", hubId, event.getId());

        sensorService.createSensor(hubId, event);
    }

    @Transactional
    public void removeDevice(String hubId, DeviceRemovedEventAvro event) {
        log.debug("RemoveDevice: hubId {}, id {}", hubId, event.getId());

        sensorService.removeByHubIdAndId(hubId, event.getId());
    }

    @Transactional
    public void addScenario(String hubId, ScenarioAddedEventAvro event) {
        Scenario scenario = scenarioRepo.findByHubIdAndName(hubId, event.getName())
                .orElseGet(() -> {
                    Scenario s = new Scenario();
                    s.setHubId(hubId);
                    s.setName(event.getName());
                    return s;
                });

        Map<String, Action> newActions = mapActions(hubId, event.getActions());
        scenario.getActions().clear();
        scenario.getActions().putAll(newActions);

        Map<String, Condition> newConditions = mapConditions(hubId, event.getConditions());
        scenario.getConditions().clear();
        scenario.getConditions().putAll(newConditions);

        scenarioRepo.save(scenario);
    }

    @Transactional
    public void removeScenario(String hubId, ScenarioRemovedEventAvro event) {
        log.debug("RemoveScenario: hubId {}, name {}", hubId, event.getName());

        Optional<Scenario> scenario = scenarioRepo.findByHubIdAndName(hubId, event.getName());
        scenario.ifPresentOrElse((sc) ->
                        scenarioRepo.removeByHubIdAndId(hubId, sc.getId()),
                () -> log.warn("Haven't found scenario: hubId {}, name {}",
                        hubId, event.getName())
        );
    }

    private Map<String, Action> mapActions(String hubId, List<DeviceActionAvro> actions) {
        return actions.stream()
                .filter(action ->
                        LogUtils.logIfEmpty(sensorService.findByIdAndHubId(action.getSensorId(), hubId), log,
                                        "Haven't found sensor: hubId {}, id {}", hubId, action.getSensorId())
                                .isPresent())
                .collect(Collectors.toMap(
                        DeviceActionAvro::getSensorId,
                        action -> Action.builder()
                                .type(action.getType())
                                .value(action.getValue())
                                .build()
                ));
    }

    private Map<String, Condition> mapConditions(String hubId, List<ScenarioConditionAvro> conditions) {
        return conditions.stream()
                .filter(condition ->
                        LogUtils.logIfEmpty(sensorService.findByIdAndHubId(condition.getSensorId(), hubId), log,
                                        "MapConditions can't find sensor hubId {}, id {}",
                                        hubId, condition.getSensorId())
                                .isPresent())
                .collect(Collectors.toMap(
                ScenarioConditionAvro::getSensorId,
                condition -> Condition.builder()
                        .operation(condition.getOperation())
                        .type(condition.getType())
                        .value(mapValue(condition.getValue()))
                        .build()
        ));
    }

    private Integer mapValue(Object value) {
        return switch (value) {
            case Integer integer -> integer;
            case Boolean bool -> bool ? 1 : 0;
            case null -> {
                log.warn("Can't map null value");
                yield null;
            }
            default -> {
                log.warn("Can't map value of type {}", value.getClass().getSimpleName());
                yield null;
            }
        };
    }
}