package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.fallback.PaymentFallbackFactory;
import ru.yandex.practicum.feign.operation.PaymentOperations;

@FeignClient(name = "payment", path = "/api/v1/payment", fallback = PaymentFallbackFactory.class)
public interface PaymentClient extends PaymentOperations {
}
