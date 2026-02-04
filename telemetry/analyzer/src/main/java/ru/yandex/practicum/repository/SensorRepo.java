package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.dao.Sensor;

public interface SensorRepo extends JpaRepository<Sensor, String> {
}
