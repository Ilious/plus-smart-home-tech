package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.fallback.OrderFallbackFactory;
import ru.yandex.practicum.feign.operation.OrderOperations;

@FeignClient(name = "order", path = "/api/v1/order", fallback = OrderFallbackFactory.class)
public interface OrderClient extends OrderOperations {
}
