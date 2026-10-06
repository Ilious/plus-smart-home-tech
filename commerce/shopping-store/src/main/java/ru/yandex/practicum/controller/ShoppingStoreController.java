package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shopping.store.ProductDto;
import ru.yandex.practicum.dto.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.feign.operation.ShoppingStoreOperations;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.UUID;

@RestController
@RequestMapping("${app.api-version}" + "/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreOperations {

    private final ShoppingStoreService storeService;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category,
                                        Pageable pageable) {
        return storeService.getProducts(category, pageable);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return storeService.getProduct(productId);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        return storeService.updateProduct(productDto);
    }

    @Override
    public ProductDto saveProduct(ProductDto productDto) {
        return storeService.createProduct(productDto);
    }

    @Override
    public void removeProductFromStore(UUID productId) {
        storeService.removeProductFromStore(productId);
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest quantityStateRequest) {
        return storeService.setProductQuantityState(quantityStateRequest);
    }
}
