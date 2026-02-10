package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shopping.store.ProductDto;
import ru.yandex.practicum.dto.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.feign.ShoppingStoreOperations;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("${app.api-version}" + "/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreOperations {

    private final ShoppingStoreService storeService;

    @Override
    public List<ProductDto> getProducts(@NotNull(message = "should have category") ProductCategory category,
                                        Pageable pageable) {
        return storeService.getProducts(category, pageable);
    }

    @Override
    public ProductDto getProduct(@PathVariable @NotNull(message = "product Id shouldn't be null") UUID productId) {
        return storeService.getProduct(productId);
    }

    @Override
    public ProductDto updateProduct(@Valid ProductDto productDto) {
        return storeService.updateProduct(productDto);
    }

    @Override
    public ProductDto saveProduct(@RequestBody @Valid ProductDto productDto) {
        return storeService.createProduct(productDto);
    }

    @Override
    public void removeProductFromStore(UUID productId) {
        storeService.removeProductFromStore(productId);
    }

    @Override
    public boolean setProductQuantityState(@RequestBody @Valid SetProductQuantityStateRequest quantityStateRequest) {
        return storeService.setProductQuantityState(quantityStateRequest);
    }
}
