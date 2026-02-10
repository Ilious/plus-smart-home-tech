package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Builder
@Getter @Setter @ToString
@AllArgsConstructor
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
