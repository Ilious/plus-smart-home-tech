package ru.yandex.practicum.dao;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Dimension {

    @Min(value = 1, message = "width should be at least 1")
    @NotNull(message = "width can't be null")
    private Double width;

    @Min(value = 1, message = "height should be at least 1")
    @NotNull(message = "height can't be null")
    private Double height;

    @Min(value = 1, message = "depth should be at least 1")
    @NotNull(message = "depth can't be null")
    private Double depth;
}
