package ru.yandex.practicum.dto.shopping.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @ToString
public class ProductDto {

    private UUID productId;

    @NotBlank(message = "product should have name")
    private String productName;

    @NotBlank(message = "description shouldn't be blank")
    private String description;

    private String imageSrc;

    @Positive(message = "price should be greater than 0")
    @NotNull(message = "price can't be null")
    private BigDecimal price;

    @NotNull(message = "Product quantity state can't be null")
    private QuantityState quantityState;

    @NotNull(message = "Product state state can't be null")
    private ProductState productState;

    @NotNull(message = "Product category can't be null")
    private ProductCategory productCategory;
}
