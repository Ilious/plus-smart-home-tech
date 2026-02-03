package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.KafkaEventProducer;
import ru.yandex.practicum.service.handler.hub.base.BaseHubEventHandler;

import java.util.List;

@Component
public class ScenarioAddedHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public ScenarioAddedEventAvro toMessage(HubEventProto hubEvent) {
        ScenarioAddedEventProto scenarioAdded = hubEvent.getScenarioAdded();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAdded.getName())
                .setActions(mapToAvroDeviceActions(scenarioAdded))
                .setConditions(mapToAvroConditions(scenarioAdded))
                .build();
    }

    private List<DeviceActionAvro> mapToAvroDeviceActions(ScenarioAddedEventProto scenarioAdded) {
        return scenarioAdded.getActionsList().stream()
                .map(this::mapToAvroDeviceAction)
                .toList();
    }

    private DeviceActionAvro mapToAvroDeviceAction(DeviceActionProto deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                .setValue(deviceAction.getValue())
                .build();
    }

    private List<ScenarioConditionAvro> mapToAvroConditions(ScenarioAddedEventProto scenarioAdded) {
        return scenarioAdded.getConditionsList().stream()
                .map(this::mapToAvroCondition)
                .toList();
    }

    private ScenarioConditionAvro mapToAvroCondition(ScenarioConditionProto condition) {
        return ScenarioConditionAvro.newBuilder()
                .setValue(getValue(condition))
                .setSensorId(condition.getSensorId())
                .setOperation(mapToConditionOperationAvro(condition))
                .setType(mapToConditionTypeAvro(condition))
                .build();
    }

    private static Integer getValue(ScenarioConditionProto condition) {
        return switch (condition.getValueCase()) {
            case INT_VALUE -> condition.getIntValue();
            case BOOL_VALUE -> condition.getBoolValue() ? 1 : 0;
            case VALUE_NOT_SET -> null;
        };
    }

    private ConditionTypeAvro mapToConditionTypeAvro(ScenarioConditionProto scenarioCondition) {
        return ConditionTypeAvro.valueOf(scenarioCondition.getType().name());
    }

    private ConditionOperationAvro mapToConditionOperationAvro(ScenarioConditionProto scenarioCondition) {
        return ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name());
    }
}
