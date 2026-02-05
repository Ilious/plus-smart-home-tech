package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.dao.Action;
import ru.yandex.practicum.dal.dao.Scenario;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
public class HubEventClient {

    @GrpcClient("hub-router")
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubClient;

    public HubEventClient(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubClient) {
        this.hubClient = hubClient;
    }

    public void handleScenario(Scenario request) {
        Instant instant = Instant.now();

        Timestamp timestampProto = Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();

        for (Map.Entry<String, Action> actionEntry: request.getActions().entrySet()) {
            String sensorId = actionEntry.getKey();
            Action actionType = actionEntry.getValue();

            DeviceActionProto.Builder deviceAction = DeviceActionProto.newBuilder()
                    .setSensorId(sensorId)
                    .setType(ActionTypeProto.valueOf(actionType.getType().name()));

            if (actionType.getType() == ActionTypeAvro.SET_VALUE)
                    deviceAction.setValue(actionType.getValue());

            try {
                log.info("Sending message, hubId {}, action {}", request.getHubId(), deviceAction);

                hubClient.handleDeviceAction(DeviceActionRequest.newBuilder()
                        .setAction(deviceAction.build())
                        .setScenarioName(request.getName())
                        .setHubId(request.getHubId())
                        .setTimestamp(timestampProto)
                        .build()
                );
            } catch (Exception ex) {
                log.error("Error sending message, hubId {}, action {}", request.getHubId(), deviceAction, ex);
            }
        }
    }
}
