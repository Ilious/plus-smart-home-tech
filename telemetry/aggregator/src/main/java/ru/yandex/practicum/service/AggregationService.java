package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AggregationService {

    Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        String hubId = event.getHubId();
        String sensorId = event.getId();

        SensorsSnapshotAvro newSnapshot = snapshots.computeIfAbsent(hubId, (key) ->
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(key)
                        .setTimestamp(Instant.EPOCH)
                        .setSensorsState(new HashMap<>())
                        .build()
        );

        Map<String, SensorStateAvro> sensorsState = newSnapshot.getSensorsState();

        if (sensorsState.containsKey(sensorId)) {
            SensorStateAvro oldState = sensorsState.get(sensorId);

            if (oldState.getTimestamp().isAfter(event.getTimestamp()) || oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newSensorState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        sensorsState.put(sensorId, newSensorState);
        newSnapshot.setTimestamp(event.getTimestamp());
        newSnapshot.setHubId(hubId);

        return Optional.of(newSnapshot);
    }
}
