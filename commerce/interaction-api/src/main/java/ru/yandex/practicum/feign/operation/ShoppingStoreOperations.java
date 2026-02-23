package ru.yandex.practicum.feign.operation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shopping.store.ProductDto;
import ru.yandex.practicum.dto.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;

import java.util.UUID;

public interface ShoppingStoreOperations {
    @GetMapping
    Page<ProductDto> getProducts(@RequestParam("category") @NotNull ProductCategory category,
                                 @PageableDefault(size = 15) Pageable pageable);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable @NotNull UUID productId);

    @PutMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    ProductDto saveProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    void removeProductFromStore(@RequestBody @NotNull UUID productId);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@ModelAttribute @Valid SetProductQuantityStateRequest quantityStateRequest);
}
