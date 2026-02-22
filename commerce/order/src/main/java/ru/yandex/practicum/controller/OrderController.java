package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.feign.operation.OrderOperations;
import ru.yandex.practicum.service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderOperations {

    private final OrderService orderService;

    @Override
    public Page<OrderDto> getOrders(String username, Pageable pageable) {
        return orderService.getOrders(username, pageable);
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest request) {
        return orderService.createOrder(request);
    }

    @Override
    public OrderDto returnOrderProducts(ProductReturnRequest request) {
        return orderService.returnOrderProducts(request);
    }

    @Override
    public OrderDto processPayment(UUID orderId) {
        return orderService.processPayment(orderId);
    }

    @Override
    public OrderDto markPaymentAsFailed(UUID orderId) {
        return orderService.markPaymentAsFailed(orderId);
    }

    @Override
    public OrderDto processDelivery(UUID orderId) {
        return orderService.processDelivery(orderId);
    }

    @Override
    public OrderDto markDeliveryAsFailed(UUID orderId) {
        return orderService.markDeliveryAsFailed(orderId);
    }

    @Override
    public OrderDto markOrderAsCompleted(UUID orderId) {
        return orderService.markOrderAsCompleted(orderId);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        return orderService.calculateTotal(orderId);
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        return orderService.calculateDelivery(orderId);
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) {
        return orderService.assemblyOrder(orderId);
    }

    @Override
    public OrderDto assemblyOrderFailed(UUID orderId) {
        return orderService.assemblyOrderFailed(orderId);
    }
}
