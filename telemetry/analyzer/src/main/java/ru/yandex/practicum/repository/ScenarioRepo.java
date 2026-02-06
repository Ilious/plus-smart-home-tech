package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.dao.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepo extends JpaRepository<Scenario, Long> {

    @Query("SELECT DISTINCT s FROM Scenario s " +
            "LEFT JOIN FETCH s.conditions " +
            "LEFT JOIN FETCH s.actions " +
            "WHERE s.hubId = :hubId")
    List<Scenario> findAllByHubId(String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    void removeByHubIdAndId(String hubId, Long id);
}
