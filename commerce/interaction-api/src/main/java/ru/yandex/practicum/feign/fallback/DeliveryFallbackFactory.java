package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.exception.ServiceNotUpException;
import ru.yandex.practicum.feign.client.DeliveryClient;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class DeliveryFallbackFactory implements FallbackFactory<DeliveryClient> {

    @Override
    public DeliveryClient create(Throwable cause) {
        log.error("Delivery link failed. Circuit opened due to: {}", cause.getMessage());

        return new DeliveryClient() {

            @Override
            public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
                log.error("Cannot create delivery for order {}. Delivery is down", deliveryDto.getOrderId());
                throw new ServiceNotUpException("Create delivery caused error", cause);
            }

            @Override
            public void markAsDelivered(UUID orderId) {
                log.error("Cannot mark as delivered for order {}. Delivery is down", orderId);
                throw new ServiceNotUpException("Mark as delivered caused error", cause);
            }

            @Override
            public void markAsInProgress(UUID orderId) {
                log.error("Cannot mark as in progress for order {}. Delivery is down", orderId);
                throw new ServiceNotUpException("Mark in progress caused error", cause);
            }

            @Override
            public void markAsFailed(UUID orderId) {
                log.error("Cannot mark as failed for order {}. Delivery is down", orderId);
                throw new ServiceNotUpException("Mark as failed caused error", cause);
            }

            @Override
            public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
                log.error("Cannot calculate delivery cost for order {}. Delivery is down", orderDto.getOrderId());
                throw new ServiceNotUpException("Calculate delivery cost caused error", cause);
            }
        };
    }
}
