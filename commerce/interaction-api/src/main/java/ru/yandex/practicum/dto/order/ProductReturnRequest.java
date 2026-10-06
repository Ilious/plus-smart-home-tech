package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Getter @Setter @ToString
public class ProductReturnRequest {

    @NotNull(message = "order id can't be null")
    private UUID orderId;

    @NotEmpty(message = "products can't be empty")
    private Map<UUID, Long> products;
}
