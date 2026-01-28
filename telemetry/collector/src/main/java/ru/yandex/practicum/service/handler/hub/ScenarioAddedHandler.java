package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hub.ScenarioAdded;
import ru.yandex.practicum.model.hub.ScenarioCondition;
import ru.yandex.practicum.model.hub.base.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;
import ru.yandex.practicum.service.KafkaEventProducer;
import ru.yandex.practicum.service.handler.hub.base.BaseHubEventHandler;

import java.util.List;

@Component
public class ScenarioAddedHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED_EVENT;
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEvent hubEvent) {
        ScenarioAdded scenarioAdded = (ScenarioAdded) hubEvent;
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAdded.getName())
                .setActions(mapToAvroActions(scenarioAdded))
                .setConditions(mapToAvroConditions(scenarioAdded))
                .build();
    }

    private List<ActionTypeAvro> mapToAvroActions(ScenarioAdded scenarioAdded) {
        return scenarioAdded.getActions().stream()
                .map(action -> ActionTypeAvro.valueOf(action.toString()))
                .toList();
    }

    private List<ScenarioConditionAvro> mapToAvroConditions(ScenarioAdded scenarioAdded) {
        return scenarioAdded.getConditions().stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setValue(condition.getValue())
                        .setSensorId(condition.getSensorId())
                        .setOperation(mapToConditionOperationAvro(condition))
                        .setType(mapToConditionTypeAvro(condition))
                        .build()
                )
                .toList();
    }

    private ConditionTypeAvro mapToConditionTypeAvro(ScenarioCondition scenarioCondition) {
        return ConditionTypeAvro.valueOf(scenarioCondition.getType().name());
    }

    private ConditionOperationAvro mapToConditionOperationAvro(ScenarioCondition scenarioCondition) {
        return ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name());
    }
}
