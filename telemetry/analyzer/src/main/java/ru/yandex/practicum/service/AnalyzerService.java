package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.Condition;
import ru.yandex.practicum.dao.Scenario;
import ru.yandex.practicum.dao.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalyzerService {

    private final ScenarioService scenarioService;

    private final SensorService sensorService;

    public List<Scenario> analyze(SensorsSnapshotAvro snapshot) {
        List<Scenario> scenariosByHubId = scenarioService.findAllByHubId(snapshot.getHubId());
        List<Scenario> completeScenarios = new ArrayList<>();

        for (Scenario scenario : scenariosByHubId)
            if (checkScenarioConditions(scenario, snapshot)) {
                completeScenarios.add(scenario);
            }

        return completeScenarios;
    }

    private boolean checkScenarioConditions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        Map<String, Condition> conditions = scenario.getConditions();
        if (conditions.isEmpty())
            return true;

        for (Map.Entry<String, Condition> conditionEntry: conditions.entrySet()) {
            String sensorId = conditionEntry.getKey();
            Condition condition = conditionEntry.getValue();
            if (!checkCondition(sensorId, condition, snapshot))
                return false;
        }

        return true;
    }

    private boolean checkCondition(String sensorId, Condition condition, SensorsSnapshotAvro snapshot) {
        ConditionTypeAvro type = condition.getType();
        Optional<Sensor> sensorById = sensorService.findByIdAndHubId(sensorId, snapshot.getHubId());

        if (sensorById.isEmpty())
            return false;

        SensorStateAvro sensorState = snapshot.getSensorsState().get(sensorId);
        if (sensorState == null)
            return false;

        Integer sensorValue = getSensorValue(sensorState, type);

        return checkConditionValue(condition, condition.getValue(), sensorValue);
    }

    private boolean checkConditionValue(Condition condition, int conditionValue, int sensorValue) {
        return switch (condition.getOperation()) {
            case EQUALS -> sensorValue == conditionValue;
            case GREATER_THAN -> conditionValue < sensorValue;
            case LOWER_THAN -> conditionValue > sensorValue;
        };
    }

    private Integer getSensorValue(SensorStateAvro sensor, ConditionTypeAvro type) {
        Object data = sensor.getData();

        return switch (type) {
            case MOTION -> ((MotionSensorAvro) data).getMotion() ? 1 : 0;
            case LUMINOSITY -> ((LightSensorAvro) data).getLuminosity();
            case SWITCH -> ((SwitchSensorAvro) data).getState() ? 1 : 0;
            case TEMPERATURE -> (data instanceof ClimateSensorAvro) ?
                    ((ClimateSensorAvro) data).getTemperatureC() :
                    ((TemperatureSensorAvro) data).getTemperatureC();
            case CO2LEVEL -> ((ClimateSensorAvro) data).getCo2Level();
            case HUMIDITY -> ((ClimateSensorAvro) data).getHumidity();
        };
    }
}
