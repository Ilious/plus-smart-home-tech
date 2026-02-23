package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;

@Getter @Setter @ToString
public class CreateNewOrderRequest {

    @NotNull(message = "cart must be not null")
    private ShoppingCartDto shoppingCart;

    @NotNull
    private AddressDto address;
}
