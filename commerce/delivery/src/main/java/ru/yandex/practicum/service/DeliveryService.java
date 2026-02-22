package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.Delivery;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.feign.client.OrderClient;
import ru.yandex.practicum.feign.client.WarehouseClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.repo.DeliveryRepo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private static final double ADDRESS2_MULTIPLIER = 2.0;

    private static final double ADDRESS1_MULTIPLIER = 1.0;

    private static final double ADDRESS_DEFAULT_MULTIPLIER = 1.0;

    private static final double DELIVERY_COST = 5.0;

    public static final double FRAGILE_MULTIPLIER = 0.2;

    public static final double WEIGHT_MULTIPLIER = 0.3;

    public static final double VOLUME_MULTIPLIER = 0.2;

    public static final double ANOTHER_STREET_MULTIPLIER = 0.2;

    private final DeliveryRepo deliveryRepo;

    private final DeliveryMapper deliveryMapper;

    private final OrderClient orderClient;

    private final WarehouseClient warehouseClient;

    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        log.debug("Creation delivery by order id: {}", deliveryDto.getOrderId());
        Delivery delivery = deliveryMapper.toEntity(deliveryDto);

        Delivery savedDelivery = deliveryRepo.save(delivery);

        return deliveryMapper.toDto(savedDelivery);
    }

    public void markAsDelivered(UUID orderId) {
        log.debug("Marking success delivery by order id: {}", orderId);

        Delivery delivery = deliveryRepo.getDeliveryByOrderId(orderId);

        if (delivery.getDeliveryState() != DeliveryState.IN_PROGRESS)
            throw new IllegalStateException(
                    String.format("Can't process delivery id %s state %s",
                            orderId, delivery.getDeliveryState()));


        delivery.setDeliveryState(DeliveryState.DELIVERED);

        orderClient.processDelivery(orderId);
    }

    public void markAsInProgress(UUID orderId) {
        log.debug("Marking in progress delivery by order id: {}", orderId);
        Delivery delivery = deliveryRepo.getDeliveryByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);

        ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(orderId)
                .build();

        warehouseClient.shippedToDelivery(request);
    }

    public void markAsFailed(UUID orderId) {
        log.debug("Marking failed delivery by order id: {}", orderId);
        Delivery delivery = deliveryRepo.getDeliveryByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.FAILED);

        orderClient.markDeliveryAsFailed(orderId);
    }

    @Cacheable(cacheNames = "delivery_costs", key = "#orderDto.orderId")
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        log.debug("Calculating delivery cost by order id: {}", orderDto.getOrderId());
        Delivery delivery = deliveryRepo.getDeliveryByOrderId(orderDto.getOrderId());

        BigDecimal cost = BigDecimal.valueOf(DELIVERY_COST);

        BigDecimal addressMultiplier = getAddressMultiplier(delivery.getFromAddress().getStreet());
        BigDecimal multipliedCost = cost.multiply(addressMultiplier);
        cost = cost.add(multipliedCost);

        if (Boolean.TRUE.equals(delivery.getFragile())) {
            BigDecimal fragileCost = cost.multiply(BigDecimal.valueOf(FRAGILE_MULTIPLIER));
            cost = cost.add(fragileCost);
        }

        BigDecimal weightCost = BigDecimal.valueOf(delivery.getDeliveryWeight())
                .multiply(BigDecimal.valueOf(WEIGHT_MULTIPLIER));
        cost = cost.add(weightCost);

        BigDecimal volumeCost = BigDecimal.valueOf(delivery.getDeliveryVolume())
                .multiply(BigDecimal.valueOf(VOLUME_MULTIPLIER));
        cost = cost.add(volumeCost);

        if (!delivery.getFromAddress().getStreet().equals(delivery.getToAddress().getStreet())) {
            BigDecimal addressCost = cost.multiply(BigDecimal.valueOf(ANOTHER_STREET_MULTIPLIER));
            cost = cost.add(addressCost);
        }

        return cost.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getAddressMultiplier(String street) {
        if (street.contains("ADDRESS_2")) {
            return BigDecimal.valueOf(ADDRESS2_MULTIPLIER);
        } else if (street.contains("ADDRESS_1")) {
            return BigDecimal.valueOf(ADDRESS1_MULTIPLIER);
        } else {
            return BigDecimal.valueOf(ADDRESS_DEFAULT_MULTIPLIER);
        }
    }
}
