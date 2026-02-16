package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter @Setter @ToString
public class BookedProductsDto {

    @NotNull(message = "weight can't be null")
    @Positive(message = "weight must be greater than 0")
    private Double deliveryWeight;

    @NotNull(message = "volume can't be null")
    @Positive(message = "volume must be greater than 0")
    private Double deliveryVolume;

    @NotNull(message = "fragile can't be null")
    private Boolean fragile;
}
