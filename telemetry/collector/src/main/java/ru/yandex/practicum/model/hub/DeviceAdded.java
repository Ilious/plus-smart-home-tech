package ru.yandex.practicum.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.hub.base.HubEvent;
import ru.yandex.practicum.model.hub.enums.DeviceType;
import ru.yandex.practicum.model.hub.enums.HubEventType;

@Getter @Setter
@ToString(callSuper = true)
public class DeviceAdded extends HubEvent {

    private String id;

    private DeviceType deviceType;

    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED_EVENT;
    }
}
