package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;

@Service
public class AnalyzerService {

    public List<ScenarioConditionAvro> analyze(SensorsSnapshotAvro snapshotAvro) {
//        for (ScenarioConditionAvro scenario : scenarios) {
//
//        }

        return null;
    }

    public List<ScenarioConditionAvro> doScenarios(List<ScenarioConditionAvro> scenarios) {
        return null;
    }
}
