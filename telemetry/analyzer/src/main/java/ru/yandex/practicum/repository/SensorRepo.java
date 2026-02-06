package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.dao.Sensor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SensorRepo extends JpaRepository<Sensor, String> {

    boolean existsByIdAndHubId(String id, String hubId);

    @Query("""
            SELECT s.id FROM Sensor s
            WHERE s.hubId = :hubId AND s.id IN :ids
            """)
    List<String> findExistingIdsByHubIdAndIdIn(@Param("hubId") String hubId, @Param("ids") Collection<String> ids);

    Optional<Sensor> findByIdAndHubId(String id, String hubId);

    void removeByHubIdAndId(String hubId, String id);
}
