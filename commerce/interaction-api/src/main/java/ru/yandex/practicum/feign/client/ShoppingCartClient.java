package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.fallback.ShoppingCartFallbackFactory;
import ru.yandex.practicum.feign.operation.ShoppingCartOperations;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart", fallback = ShoppingCartFallbackFactory.class)
public interface ShoppingCartClient extends ShoppingCartOperations {
}
