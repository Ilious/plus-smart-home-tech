package ru.yandex.practicum.feign.operation;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperations {

    @PostMapping
    PaymentDto generatePaymentInfo(@RequestBody @Valid OrderDto order);

    @PostMapping("/totalCost")
    BigDecimal calculateTotalCost(@RequestBody @Valid OrderDto order);

    @PostMapping("/productCost")
    BigDecimal calculateProductCost(@RequestBody @Valid OrderDto order);

    @PostMapping("/refund")
    void markPaymentAsSuccessful(@RequestBody UUID paymentId);

    @PostMapping("/failed")
    void markPaymentAsFailed(@RequestBody UUID paymentId);
}
