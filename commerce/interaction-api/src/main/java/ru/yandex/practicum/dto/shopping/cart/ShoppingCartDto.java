package ru.yandex.practicum.dto.shopping.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Getter @Setter @ToString
public class ShoppingCartDto {

    @NotNull(message = "shoppingCartId can't be null")
    private UUID shoppingCartId;

    @NotNull(message = "product ids can't be null")
    private Map<UUID, Long> products;
}
