package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter @Setter @ToString
@AllArgsConstructor
public class AddProductToWarehouseRequest {

    @NotNull(message = "request should have product Id")
    private UUID productId;

    @NotNull(message = "quantity can't be null")
    @Positive(message = "quantity should be greater than 0")
    private Long quantity;
}
