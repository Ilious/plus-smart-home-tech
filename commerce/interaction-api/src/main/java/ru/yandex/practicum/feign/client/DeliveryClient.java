package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.fallback.DeliveryFallbackFactory;
import ru.yandex.practicum.feign.operation.DeliveryOperations;

@FeignClient(name = "delivery", path = "/api/v1/delivery", fallback = DeliveryFallbackFactory.class)
public interface DeliveryClient extends DeliveryOperations {
}
