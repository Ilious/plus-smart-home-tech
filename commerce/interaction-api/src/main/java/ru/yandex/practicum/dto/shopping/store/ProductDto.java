package ru.yandex.practicum.dto.shopping.store;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@Getter @Setter @ToString
@AllArgsConstructor
public class ProductDto {

    private UUID productId;

    @Max(value = 100, message = "size should be 100 letters at most")
    @NotBlank(message = "product should have name")
    private String productName;

    @Size(max=255, message = "size should be 255 letters at most")
    @NotBlank(message = "description shouldn't be blank")
    private String description;

    private String imageSrc;

    @Positive(message = "price should be greater than 0")
    @NotNull(message = "price can't be null")
    private Integer price;

    @NotNull(message = "Product quantity state can't be null")
    private QuantityState quantityState;

    @NotNull(message = "Product state state can't be null")
    private ProductState productState;

    @NotNull(message = "Product category can't be null")
    private ProductCategory category;
}
