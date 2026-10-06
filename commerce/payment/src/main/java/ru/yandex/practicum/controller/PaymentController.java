package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.feign.operation.PaymentOperations;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentOperations {

    private final PaymentService paymentService;

    @Override
    public PaymentDto generatePaymentInfo(OrderDto order) {
        return paymentService.generatePaymentInfo(order);
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto order) {
        return paymentService.calculateTotalCost(order);
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto order) {
        return paymentService.calculateProductCost(order);
    }

    @Override
    public void markPaymentAsSuccessful(UUID paymentId) {
        paymentService.markPaymentAsSuccessful(paymentId);
    }

    @Override
    public void markPaymentAsFailed(UUID paymentId) {
        paymentService.markPaymentAsFailed(paymentId);
    }
}
