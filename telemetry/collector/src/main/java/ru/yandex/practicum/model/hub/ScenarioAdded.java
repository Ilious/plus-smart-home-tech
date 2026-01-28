package ru.yandex.practicum.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.hub.base.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

import java.util.List;

@Getter @Setter
@ToString(callSuper = true)
public class ScenarioAdded extends HubEvent {

    private String name;

    private List<ScenarioCondition> conditions;

    private List<DeviceAction> actions;

    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED_EVENT;
    }
}
