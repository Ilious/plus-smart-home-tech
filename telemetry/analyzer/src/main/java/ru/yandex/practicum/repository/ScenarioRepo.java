package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.dao.Scenario;

public interface ScenarioRepo extends JpaRepository<Scenario, Long> {
}
