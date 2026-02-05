package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.dao.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepo extends JpaRepository<Scenario, Long> {

    List<Scenario> findAllByHubId(String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    void removeByHubIdAndId(String hubId, Long id);

}
