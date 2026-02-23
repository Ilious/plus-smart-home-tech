package ru.yandex.practicum.feign.operation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryOperations {

    @PutMapping
    DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void markAsDelivered(@RequestBody @NotNull UUID orderId);

    @PostMapping("/picked")
    void markAsInProgress(@RequestBody @NotNull UUID orderId);

    @PostMapping("/failed")
    void markAsFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/cost")
    BigDecimal calculateDeliveryCost(@RequestBody @Valid OrderDto orderDto);
}
