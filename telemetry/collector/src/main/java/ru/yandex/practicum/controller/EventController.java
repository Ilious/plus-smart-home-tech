package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.base.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;
import ru.yandex.practicum.model.sensor.base.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;
import ru.yandex.practicum.service.handler.HubEventHandler;
import ru.yandex.practicum.service.handler.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    private final Map<SensorEventType, SensorEventHandler> sensorsEvents;

    private final Map<HubEventType, HubEventHandler> hubEvents;

    public EventController(Set<SensorEventHandler> sensors, Set<HubEventHandler> hubs) {
        this.sensorsEvents = sensors.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEvents = hubs.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        log.info("Registered sensor handlers {}", sensorsEvents);
    }

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("json sensorEvent {}", event);
        SensorEventHandler handler = sensorsEvents.get(event.getType());

        if (handler == null) {
            throw new IllegalArgumentException("Can't find handler for event " + event.getType());
        }
        handler.handle(event);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("json hubEvent {}", event);
        HubEventHandler handler = hubEvents.get(event.getType());

        if (handler == null) {
            throw new IllegalArgumentException("Can't find handler for hub " + event.getType());
        }
        handler.handle(event);
    }
}
