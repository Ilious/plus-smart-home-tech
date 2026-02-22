package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.mapper.SensorMapper;
import ru.yandex.practicum.repository.SensorRepo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepo sensorRepo;

    private final SensorMapper sensorMapper;

    public void createSensor(String hubId, DeviceAddedEventAvro event) {
        String id = event.getId();
        log.debug("Creation sensor: hubId {}, id {}", hubId, id);
        if (sensorRepo.existsByIdAndHubId(event.getId(), hubId)) {
            log.debug("Sensor id = {} already exists, skipping creation", event.getId());
            return;
        }

        sensorRepo.save(sensorMapper.toSensor(hubId, event));
    }

    @CacheEvict(cacheNames = "sensors", key = "#id + '-' + #hubId")
    public void removeByHubIdAndId(String hubId, String id) {
        log.debug("Removing sensor: hubId {}, id {}", hubId, id);

        sensorRepo.removeByHubIdAndId(hubId, id);
    }

    @Cacheable(cacheNames = "sensors", key = "#id + '-' + #hubId")
    @Transactional(readOnly = true)
    public Optional<Sensor> findByIdAndHubId(String id, String hubId) {
        log.debug("Find sensor: hubId {}, id {}", hubId, id);

        return sensorRepo.findByIdAndHubId(id, hubId);
    }

    @Transactional(readOnly = true)
    public List<String> findExistingIdsByHubIdAndIdIn(String hubId, Collection<String> ids) {
        log.debug("findExistingIdsByHubIdAndIdIn: hubId {}, ids.size() {}", hubId, ids.size());

        return sensorRepo.findExistingIdsByHubIdAndIdIn(hubId, ids);
    }
}
