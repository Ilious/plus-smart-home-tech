package ru.yandex.practicum.feign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shopping.store.ProductDto;
import ru.yandex.practicum.dto.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

public interface ShoppingStoreOperations {
    @GetMapping
    List<ProductDto> getProducts(@NotNull(message = "should have category") ProductCategory category,
                                 Pageable pageable);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable @NotNull(message = "product Id shouldn't be null") UUID productId);

    @PutMapping
    ProductDto updateProduct(@Valid ProductDto productDto);

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    ProductDto saveProduct(@RequestBody @Valid ProductDto productDto);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/removeProductFromStore")
    void removeProductFromStore(UUID productId);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@RequestBody @Valid SetProductQuantityStateRequest quantityStateRequest);
}
