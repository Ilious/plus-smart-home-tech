package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.shopping.store.ProductDto;
import ru.yandex.practicum.dto.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.exception.ServiceNotUpException;
import ru.yandex.practicum.feign.client.ShoppingStoreClient;

import java.util.UUID;

@Slf4j
@Component
public class ShoppingStoreFallbackFactory implements FallbackFactory<ShoppingStoreClient> {
    @Override
    public ShoppingStoreClient create(Throwable cause) {
        log.error("Shopping-store link failed. Circuit opened due to: {}", cause.getMessage());

        return new ShoppingStoreClient() {

            @Override
            public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
                log.error("Cannot get products by category {}. Shopping-store is down", category);
                throw new ServiceNotUpException("Shopping-store get products failed", cause);
            }

            @Override
            public ProductDto getProduct(UUID productId) {
                log.error("Cannot get product by product {}. Shopping-store is down", productId);
                throw new ServiceNotUpException("Shopping-store get product failed", cause);
            }

            @Override
            public ProductDto updateProduct(ProductDto productDto) {
                log.error("Cannot update product by product {}. Shopping-store is down", productDto.getProductName());
                throw new ServiceNotUpException("Shopping-store update product failed", cause);
            }

            @Override
            public ProductDto saveProduct(ProductDto productDto) {
                log.error("Cannot save product by product {}. Shopping-store is down", productDto.getProductName());
                throw new ServiceNotUpException("Shopping-store save product failed", cause);
            }

            @Override
            public void removeProductFromStore(UUID productId) {
                log.error("Cannot remove product from store by product {}. Shopping-store is down", productId);
                throw new ServiceNotUpException("Shopping-store remove products service is unavailable", cause);
            }

            @Override
            public boolean setProductQuantityState(SetProductQuantityStateRequest quantityStateRequest) {
                log.error("Cannot set product quantity state by product {}. Shopping-store is down",
                        quantityStateRequest.getProductId());
                throw new ServiceNotUpException("Shopping-store set product quantity state failed", cause);
            }
        };
    }
}
