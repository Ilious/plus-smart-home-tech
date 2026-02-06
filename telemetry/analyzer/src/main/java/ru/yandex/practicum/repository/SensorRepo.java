package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dao.Sensor;

import java.util.Optional;

public interface SensorRepo extends JpaRepository<Sensor, String> {

    boolean existsByIdAndHubId(String id, String hubId);

    Optional<Sensor> findByIdAndHubId(String id, String hubId);

    void removeByHubIdAndId(String hubId, String id);
}
