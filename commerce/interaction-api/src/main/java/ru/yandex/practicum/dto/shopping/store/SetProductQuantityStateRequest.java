package ru.yandex.practicum.dto.shopping.store;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@Setter @Getter
public class SetProductQuantityStateRequest {

    @NotNull(message = "product Id shouldn't be null")
    private UUID productId;

    @NotNull(message = "Product quantity state can't be null")
    private QuantityState quantityState;
}
