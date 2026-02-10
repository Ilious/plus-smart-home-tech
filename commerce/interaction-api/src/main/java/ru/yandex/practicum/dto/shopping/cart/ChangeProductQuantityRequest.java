package ru.yandex.practicum.dto.shopping.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter @Setter @ToString
public class ChangeProductQuantityRequest {

    @NotNull(message = "request should have product Id")
    private UUID productId;

    @NotNull(message = "new quantity shouldn't be null")
    @PositiveOrZero(message = "quantity should be 0 or greater")
    private Long newQuantity;
}
