package ru.yandex.practicum.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Getter @Setter @ToString
public class WarehouseProduct {

    @Id
    private UUID productId;

    @Embedded
    private Dimension dimension;

    @Positive(message = "weight should be greater that 0")
    @Column(nullable = false)
    private Double weight;

    private Boolean fragile;

    @PositiveOrZero(message = "quantity should be 0 or greater")
    @Column(nullable = false)
    private Long quantity;
}
