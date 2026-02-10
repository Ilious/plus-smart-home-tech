package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@AllArgsConstructor
public class DimensionDto {

    @NotNull(message = "width can't be null")
    @Positive(message = "width should be greater than 0")
    private Double width;

    @NotNull(message = "height can't be null")
    @Positive(message = "height should be greater than 0")
    private Double height;

    @NotNull(message = "depth can't be null")
    @Positive(message = "depth should be greater than 0")
    private Double depth;
}
