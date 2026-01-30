package ru.yandex.practicum.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.hub.base.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

@Getter @Setter
@ToString(callSuper = true)
public class ScenarioRemoved extends HubEvent {

    private String name;

    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED_EVENT;
    }
}
