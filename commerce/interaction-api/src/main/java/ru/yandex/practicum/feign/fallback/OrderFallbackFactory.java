package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.exception.ServiceNotUpException;
import ru.yandex.practicum.feign.client.OrderClient;

import java.util.UUID;

@Slf4j
@Component
public class OrderFallbackFactory implements FallbackFactory<OrderClient> {

    @Override
    public OrderClient create(Throwable cause) {
        log.error("Order link failed. Circuit opened due to: {}", cause.getMessage());

        return new OrderClient() {
            @Override
            public Page<OrderDto> getOrders(String username, Pageable pageable) {
                log.error("Cannot get orders by username {}. Order is down", username);
                throw new ServiceNotUpException("Get orders failed", cause);
            }

            @Override
            public OrderDto createOrder(CreateNewOrderRequest request) {
                log.error("Cannot create order by cart {}. Order is down",
                        request.getShoppingCart().getShoppingCartId());
                throw new ServiceNotUpException("Create order failed", cause);
            }

            @Override
            public OrderDto returnOrderProducts(ProductReturnRequest request) {
                log.error("Cannot return order products for order {}. Order is down", request.getOrderId());
                throw new ServiceNotUpException("Return order products failed", cause);
            }

            @Override
            public OrderDto processPayment(UUID orderId) {
                log.error("Cannot process payment by order {}. Order is down", orderId);
                throw new ServiceNotUpException("Process payment failed", cause);
            }

            @Override
            public OrderDto markPaymentAsFailed(UUID orderId) {
                log.error("Cannot mark payment as failed order {}. Order is down", orderId);
                throw new ServiceNotUpException("Mark payment as failed unavailable", cause);
            }

            @Override
            public OrderDto processDelivery(UUID orderId) {
                log.error("Cannot process delivery order {}. Order is down", orderId);
                throw new ServiceNotUpException("Process delivery failed", cause);
            }

            @Override
            public OrderDto markDeliveryAsFailed(UUID orderId) {
                log.error("Cannot mark delivery as failed order {}. Order is down", orderId);
                throw new ServiceNotUpException("Mark delivery as failed unavailable", cause);
            }

            @Override
            public OrderDto markOrderAsCompleted(UUID orderId) {
                log.error("Cannot mark order as completed order {}. Order is down", orderId);
                throw new ServiceNotUpException("Mark order as completed failed", cause);
            }

            @Override
            public OrderDto calculateTotal(UUID orderId) {
                log.error("Cannot calculate total order {}. Order is down", orderId);
                throw new ServiceNotUpException("Calculate total failed", cause);
            }

            @Override
            public OrderDto calculateDelivery(UUID orderId) {
                log.error("Cannot calculate delivery order {}. Order is down", orderId);
                throw new ServiceNotUpException("Calculate delivery failed", cause);
            }

            @Override
            public OrderDto assemblyOrder(UUID orderId) {
                log.error("Cannot assembly order {}. Order is down", orderId);
                throw new ServiceNotUpException("Assembly order failed", cause);
            }

            @Override
            public OrderDto assemblyOrderFailed(UUID orderId) {
                log.error("Cannot assembly order failed {}. Order is down", orderId);
                throw new ServiceNotUpException("Assembly order failed unavailable", cause);
            }
        };
    }
}
