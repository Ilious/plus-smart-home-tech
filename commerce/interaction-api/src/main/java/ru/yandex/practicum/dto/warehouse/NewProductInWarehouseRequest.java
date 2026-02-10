package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter @Setter @ToString
public class NewProductInWarehouseRequest {

    @NotNull(message = "product Id shouldn't be null")
    private UUID productId;

    private Boolean fragile;

    @NotNull(message = "dimension can't be null")
    private DimensionDto dimensionDto;

    @NotNull(message = "weight can't be null")
    @Positive(message = "weight shouldn't be greater than 0")
    private Double weight;
}
