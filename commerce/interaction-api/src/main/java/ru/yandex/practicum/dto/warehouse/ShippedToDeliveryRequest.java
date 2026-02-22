package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShippedToDeliveryRequest {

    @NotNull(message = "delivery id can't be null")
    private UUID deliveryId;

    @NotNull(message = "order id can't be null")
    private UUID orderId;
}

