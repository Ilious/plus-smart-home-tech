package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dal.dao.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.repository.SensorRepo;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepo sensorRepo;

    public void createSensor(String hubId, DeviceAddedEventAvro deviceAddedEvent) {
        String id = deviceAddedEvent.getId();
        log.debug("CreateSensor: hubId {}, id {}", hubId, id);

        if (sensorRepo.existsByIdAndHubId(id, hubId)) {
            log.warn("Can't create Sensor hubId {}, id {} device already exists", hubId, id);
            return;
        }

        Sensor sensor = Sensor.builder()
                .id(deviceAddedEvent.getId())
                .hubId(hubId)
                .build();
        sensorRepo.save(sensor);
    }

    public void removeByHubIdAndId(String hubId, String id) {
        log.debug("RemoveByHubIdAndId: hubId {}, id {}", hubId, id);

        sensorRepo.removeByHubIdAndId(hubId, id);
    }

    @Transactional(readOnly = true)
    public Optional<Sensor> findByIdAndHubId(String id, String hubId) {
        log.debug("FindByIdAndHubId: hubId {}, id {}", hubId, id);

        return sensorRepo.findByIdAndHubId(id, hubId);
    }
}
