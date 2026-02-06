package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.helper.ScenarioMappingHelper;
import ru.yandex.practicum.repository.ScenarioRepo;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepo scenarioRepo;

    private final SensorService sensorService;

    private final ScenarioMappingHelper scenarioMapper;

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
        log.debug("Addition scenario: hubId {}, name {}", hubId, event.getName());

        Scenario scenario = scenarioMapper.buildScenario(hubId, event);

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
}