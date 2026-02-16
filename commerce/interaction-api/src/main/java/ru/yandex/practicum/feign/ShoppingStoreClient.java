package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shopping-store", path = "api/v1/shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreOperations {
}
