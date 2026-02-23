package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.exception.ServiceNotUpException;
import ru.yandex.practicum.feign.client.PaymentClient;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class PaymentFallbackFactory implements FallbackFactory<PaymentClient> {
    @Override
    public PaymentClient create(Throwable cause) {
        log.error("Payment link failed. Circuit opened due to: {}", cause.getMessage());

        return new PaymentClient() {
            @Override
            public PaymentDto generatePaymentInfo(OrderDto order) {
                log.error("Cannot generate payment info for order {}. Payment is down", order.getOrderId());
                throw new ServiceNotUpException("Generate payment info failed", cause);
            }

            @Override
            public BigDecimal calculateTotalCost(OrderDto order) {
                log.error("Cannot calculate total cost for order {}. Payment is down", order.getOrderId());
                throw new ServiceNotUpException("Calculate total cost failed", cause);
            }

            @Override
            public BigDecimal calculateProductCost(OrderDto order) {
                log.error("Cannot calculate product cost for order {}. Payment is down", order.getOrderId());
                throw new ServiceNotUpException("Calculate product cost failed", cause);
            }

            @Override
            public void markPaymentAsSuccessful(UUID paymentId) {
                log.error("Cannot mark payment as successful for payment {}. Payment is down", paymentId);
                throw new ServiceNotUpException("Mark payment as successful failed", cause);
            }

            @Override
            public void markPaymentAsFailed(UUID paymentId) {
                log.error("Cannot mark payment as failed for payment {}. Payment is down", paymentId);
                throw new ServiceNotUpException("Mark payment as failed unavailable", cause);
            }
        };
    }
}
