package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.fallback.ShoppingStoreFallbackFactory;
import ru.yandex.practicum.feign.operation.ShoppingStoreOperations;

@FeignClient(name = "shopping-store", path = "api/v1/shopping-store", fallback = ShoppingStoreFallbackFactory.class)
public interface ShoppingStoreClient extends ShoppingStoreOperations {
}
