package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.Payment;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.feign.client.OrderClient;
import ru.yandex.practicum.feign.client.ShoppingStoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.repo.PaymentRepo;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepo paymentRepo;

    private final PaymentMapper paymentMapper;

    private final OrderClient orderClient;

    private final ShoppingStoreClient shoppingStoreClient;

    public PaymentDto generatePaymentInfo(OrderDto order) {
        log.debug("generating payment info by order id {}", order.getOrderId());
        BigDecimal productCost = calculateProductCost(order);
        BigDecimal totalCost = calculateTotalCost(order);

        Payment payment = Payment.builder()
                .orderId(order.getOrderId())
                .totalSum(totalCost)
                .totalDelivery(order.getDeliveryPrice())
                .totalFee(calculateTotalFee(productCost))
                .state(PaymentState.PENDING)
                .build();

        Payment savedPayment = paymentRepo.save(payment);
        return paymentMapper.toDto(savedPayment);
    }

    public void markPaymentAsSuccessful(UUID paymentId) {
        log.debug("mark payment as success by id {}", paymentId);
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(paymentId), Payment.class));
        orderClient.processPayment(payment.getOrderId());
        payment.setState(PaymentState.SUCCESS);
    }

    public void markPaymentAsFailed(UUID paymentId) {
        log.debug("mark payment as failed by id {}", paymentId);
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(paymentId), Payment.class));
        orderClient.markPaymentAsFailed(payment.getOrderId());
        payment.setState(PaymentState.FAILED);
    }

    public BigDecimal calculateProductCost(OrderDto order) {
        log.debug("calculate product cost by order id {}", order.getOrderId());
        Map<UUID, Long> products = order.getProducts();
        if (products == null || products.isEmpty())
            throw new IllegalArgumentException("products in order can't be empty");

        BigDecimal totalCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            if (quantity == null || quantity <= 0)
                throw new IllegalArgumentException(String.format("Incorrect product quantity id %s", productId));

            try {
                BigDecimal productPrice = shoppingStoreClient.getProduct(productId).getPrice();

                BigDecimal productTotalCost = productPrice.multiply(BigDecimal.valueOf(quantity));

                totalCost = totalCost.add(productTotalCost);

            } catch (Exception e) {
                log.warn("Error getting price product id {}", productId, e);
                throw new IllegalArgumentException(String.format("Can't get product price id %s", productId), e);
            }
        }
        return totalCost;
    }

    public BigDecimal calculateTotalCost(OrderDto order) {
        log.debug("calculate total cost by order id {}", order.getOrderId());
        BigDecimal productCost = calculateProductCost(order);

        BigDecimal deliveryCost = order.getDeliveryPrice() != null
                ? order.getDeliveryPrice()
                : BigDecimal.ZERO;

        BigDecimal feeCost = calculateTotalFee(productCost);

        return productCost.add(deliveryCost).add(feeCost);
    }

    private BigDecimal calculateTotalFee(BigDecimal productPrice) {
        log.debug("calculate total fee by product price {}", productPrice);
        if (productPrice == null)
            return BigDecimal.ZERO;

        return productPrice.multiply(new BigDecimal("0.10"));
    }
}
