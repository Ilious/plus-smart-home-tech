package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dal.dao.Action;
import ru.yandex.practicum.dal.dao.Condition;
import ru.yandex.practicum.dal.dao.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.*;
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
    public void addDevice(String hubId, DeviceAddedEventAvro deviceAddedEvent) {
        log.debug("AddDevice: hubId {}, id {}", hubId, deviceAddedEvent.getId());

        sensorService.createSensor(hubId, deviceAddedEvent);
    }

    @Transactional
    public void removeDevice(String hubId, DeviceRemovedEventAvro deviceRemovedEvent) {
        log.debug("RemoveDevice: hubId {}, id {}", hubId, deviceRemovedEvent.getId());

        sensorService.removeByHubIdAndId(hubId, deviceRemovedEvent.getId());
    }

    @Transactional
    public void addScenario(String hubId, ScenarioAddedEventAvro scenarioAddedEvent) {
        Optional<Scenario> scenarioByIdAndName = scenarioRepo.findByHubIdAndName(hubId, scenarioAddedEvent.getName());
        if (scenarioByIdAndName.isPresent()) {
            log.warn("AddScenario can't create Scenario hubId {}, name {} device already exists",
                    hubId, scenarioAddedEvent.getName());
            return;
        }

        Scenario scenario = Scenario.builder()
                .hubId(hubId)
                .name(scenarioAddedEvent.getName())
                .actions(mapActions(hubId, scenarioAddedEvent.getActions()))
                .conditions(mapConditions(hubId, scenarioAddedEvent.getConditions()))
                .build();
        scenarioRepo.save(scenario);
    }

    @Transactional
    public void removeScenario(String hubId, ScenarioRemovedEventAvro scenarioRemovedEvent) {
        log.debug("RemoveScenario: hubId {}, name {}", hubId, scenarioRemovedEvent.getName());

        Optional<Scenario> scenario = scenarioRepo.findByHubIdAndName(hubId, scenarioRemovedEvent.getName());
        scenario.ifPresentOrElse((sc) ->
                        scenarioRepo.removeByHubIdAndId(hubId, sc.getId()),
                () -> log.warn("Haven't found scenario: hubId {}, name {}",
                        hubId, scenarioRemovedEvent.getName())
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