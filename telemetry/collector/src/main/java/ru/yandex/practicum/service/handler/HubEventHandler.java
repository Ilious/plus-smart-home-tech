package ru.yandex.practicum.service.handler;

import ru.yandex.practicum.model.hub.base.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

public interface HubEventHandler {

    HubEventType getMessageType();

    void handle(HubEvent event);
}
