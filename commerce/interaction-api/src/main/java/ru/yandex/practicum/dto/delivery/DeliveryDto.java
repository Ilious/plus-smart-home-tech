package ru.yandex.practicum.dto.delivery;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.enums.DeliveryState;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class DeliveryDto {

    private UUID deliveryId;

    @NotNull(message = "Sender address must be")
    private AddressDto fromAddress;

    @NotNull(message = "Receiver address must be")
    private AddressDto toAddress;

    @NotNull
    private UUID orderId;

    @NotNull(message = "Weight can't be null")
    @Positive(message = "Weight should be positive")
    private Double deliveryWeight;

    @NotNull(message = "Volume can't be null")
    @Positive(message = "Volume should be positive")
    private Double deliveryVolume;

    @NotNull(message = "Fragile can't be null")
    private Boolean fragile;

    private DeliveryState deliveryState;
}
