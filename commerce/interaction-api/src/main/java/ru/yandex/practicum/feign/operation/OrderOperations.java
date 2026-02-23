package ru.yandex.practicum.feign.operation;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.UUID;

public interface OrderOperations {


    @GetMapping
    Page<OrderDto> getOrders(@RequestParam String username,
                             @PageableDefault(size = 15, direction = Sort.Direction.DESC) Pageable pageable);

    @PutMapping
    OrderDto createOrder(@RequestBody @Valid CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto returnOrderProducts(@RequestBody @Valid ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto processPayment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto markPaymentAsFailed(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto processDelivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto markDeliveryAsFailed(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto markOrderAsCompleted(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotal(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDelivery(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyOrderFailed(@RequestBody UUID orderId);
}
