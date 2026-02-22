package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.fallback.WarehouseFallbackFactory;
import ru.yandex.practicum.feign.operation.WarehouseOperations;

@FeignClient(name = "warehouse", path = "api/v1/warehouse", fallback = WarehouseFallbackFactory.class)
public interface WarehouseClient extends WarehouseOperations {
}
