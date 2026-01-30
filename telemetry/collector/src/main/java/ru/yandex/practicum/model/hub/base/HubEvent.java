package ru.yandex.practicum.model.hub.base;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.hub.DeviceAdded;
import ru.yandex.practicum.model.hub.DeviceRemoved;
import ru.yandex.practicum.model.hub.ScenarioAdded;
import ru.yandex.practicum.model.hub.ScenarioRemoved;
import ru.yandex.practicum.model.hub.enums.HubEventType;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAdded.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemoved.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAdded.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemoved.class, name = "SCENARIO_REMOVED")
})
@Getter @Setter
@ToString
public abstract class HubEvent {

    @NotBlank
    private String hubId;

    private Instant timestamp = Instant.now();

    public abstract HubEventType getType();
}
