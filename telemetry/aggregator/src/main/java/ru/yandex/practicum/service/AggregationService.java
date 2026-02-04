package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AggregationService {

    Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        SensorsSnapshotAvro newSnapshot = snapshots.getOrDefault(event.getHubId(), new SensorsSnapshotAvro());

        if (newSnapshot.getSensorsState() != null && newSnapshot.getSensorsState().containsKey(event.getId())) {
            SensorStateAvro oldState = newSnapshot.getSensorsState().get(event.getId());

            if (oldState.getTimestamp().isAfter(event.getTimestamp()) || oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }

            SensorStateAvro newSensorState = SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event.getPayload())
                    .build();

            snapshots.get(event.getHubId()).getSensorsState().put(event.getId(), newSensorState);
            newSnapshot.setTimestamp(event.getTimestamp());
        }

        return Optional.of(newSnapshot);
    }


}
