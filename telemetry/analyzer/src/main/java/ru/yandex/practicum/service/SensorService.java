package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.mapper.SensorMapper;
import ru.yandex.practicum.repository.SensorRepo;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepo sensorRepo;

    public void createSensor(String hubId, DeviceAddedEventAvro event) {
        String id = event.getId();
        log.debug("Creation sensor: hubId {}, id {}", hubId, id);
        if (sensorRepo.existsByIdAndHubId(event.getId(), hubId)) {
            log.debug("Sensor id = {} already exists, skipping creation", event.getId());
            return;
        }

        Sensor sensor = Sensor.builder()
                .id(event.getId())
                .hubId(hubId)
                .build();
        sensorRepo.save(sensor);
    }

    public void removeByHubIdAndId(String hubId, String id) {
        log.debug("Removing sensor: hubId {}, id {}", hubId, id);

        sensorRepo.removeByHubIdAndId(hubId, id);
    }

    @Transactional(readOnly = true)
    public Optional<Sensor> findByIdAndHubId(String id, String hubId) {
        log.debug("Find sensor: hubId {}, id {}", hubId, id);

        return sensorRepo.findByIdAndHubId(id, hubId);
    }
}
