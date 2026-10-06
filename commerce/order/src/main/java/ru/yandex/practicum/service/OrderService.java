package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.Order;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exception.NotAuthorizedException;
import ru.yandex.practicum.feign.client.DeliveryClient;
import ru.yandex.practicum.feign.client.PaymentClient;
import ru.yandex.practicum.feign.client.WarehouseClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repo.OrderRepo;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;

    private final OrderMapper orderMapper;

    private final WarehouseClient warehouseClient;

    private final PaymentClient paymentClient;

    private final DeliveryClient deliveryClient;

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrders(String username, Pageable pageable) {
        log.debug("ger orders for user {}", username);
        if (username == null || username.isBlank())
            throw new NotAuthorizedException(username);

        Page<Order> orders = orderRepo.findByUsername(username, pageable);
        return orders.map(orderMapper::toDto);
    }

    public OrderDto createOrder(CreateNewOrderRequest request) {
        log.debug("Creation order in city {}", request.getAddress().getCity());
        BookedProductsDto bookedProducts = warehouseClient.checkProductAvailability(request.getShoppingCart());

        AddressDto warehouseAddress = warehouseClient.getAddress();

        Order order = orderMapper.toEntity(request).toBuilder()
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .fragile(bookedProducts.getFragile())
                .build();

        Order savedOrder = orderRepo.save(order);

        DeliveryDto delivery = DeliveryDto.builder()
                .deliveryId(UUID.randomUUID())
                .fromAddress(warehouseAddress)
                .toAddress(request.getAddress())
                .orderId(savedOrder.getOrderId())
                .deliveryWeight(savedOrder.getDeliveryWeight())
                .deliveryVolume(savedOrder.getDeliveryVolume())
                .fragile(savedOrder.getFragile())
                .deliveryState(DeliveryState.CREATED)
                .build();

        DeliveryDto createdDelivery = deliveryClient.createDelivery(delivery);

        savedOrder.setDeliveryId(createdDelivery.getDeliveryId());

        BigDecimal deliveryPrice = deliveryClient.calculateDeliveryCost(orderMapper.toDto(savedOrder));
        savedOrder.setDeliveryPrice(deliveryPrice);

        BigDecimal productPrice = paymentClient.calculateProductCost(orderMapper.toDto(savedOrder));
        savedOrder.setProductPrice(productPrice);

        BigDecimal totalPrice = paymentClient.calculateTotalCost(orderMapper.toDto(savedOrder));
        savedOrder.setTotalPrice(totalPrice);

        Order orderWithDetails = orderRepo.save(savedOrder);

        return orderMapper.toDto(orderWithDetails);
    }

    public OrderDto returnOrderProducts(ProductReturnRequest request) {
        log.debug("return order products by id {}", request.getOrderId());

        Order order = orderRepo.getOrderById(request.getOrderId());
        warehouseClient.acceptReturn(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        Order savedOrder = orderRepo.save(order);

        return orderMapper.toDto(savedOrder);
    }

    public OrderDto markPaymentAsFailed(UUID orderId) {
        log.debug("handle payment order failed by id {}", orderId);

        Order order = orderRepo.getOrderById(orderId);
        paymentClient.markPaymentAsFailed(order.getOrderId());
        order.setState(OrderState.PAYMENT_FAILED);
        Order savedOrder = orderRepo.save(order);

        return orderMapper.toDto(savedOrder);
    }

    public OrderDto processPayment(UUID orderId) {
        log.debug("payment order by id {}", orderId);

        Order order = orderRepo.getOrderById(orderId);
        paymentClient.markPaymentAsSuccessful(order.getPaymentId());
        order.setState(OrderState.PAID);
        Order savedOrder = orderRepo.save(order);

        return orderMapper.toDto(savedOrder);
    }

    public OrderDto processDelivery(UUID orderId) {
        log.debug("handle delivery order by id {}", orderId);
        Order order = orderRepo.getOrderById(orderId);

        deliveryClient.markAsInProgress(order.getOrderId());
        order.setState(OrderState.DELIVERED);
        Order savedOrder = orderRepo.save(order);

        return orderMapper.toDto(savedOrder);
    }

    public OrderDto markDeliveryAsFailed(UUID orderId) {
        log.debug("handle delivery failed order by id {}", orderId);
        Order order = orderRepo.getOrderById(orderId);

        deliveryClient.markAsFailed(order.getOrderId());
        order.setState(OrderState.DELIVERY_FAILED);
        Order savedOrder = orderRepo.save(order);

        return orderMapper.toDto(savedOrder);
    }

    public OrderDto markOrderAsCompleted(UUID orderId) {
        log.debug("handle completed order by id {}", orderId);
        Order order = orderRepo.getOrderById(orderId);

        order.setState(OrderState.COMPLETED);
        Order savedOrder = orderRepo.save(order);

        return orderMapper.toDto(savedOrder);
    }

    public OrderDto calculateTotal(UUID orderId) {
        log.debug("calculate total by id {}", orderId);
        Order order = orderRepo.getOrderById(orderId);

        BigDecimal totalPrice = paymentClient.calculateTotalCost(orderMapper.toDto(order));
        order.setTotalPrice(totalPrice);

        return orderMapper.toDto(orderRepo.save(order));
    }

    public OrderDto calculateDelivery(UUID orderId) {
        log.debug("calculate delivery by id {}", orderId);
        Order order = orderRepo.getOrderById(orderId);

        BigDecimal deliveryPrice = deliveryClient.calculateDeliveryCost(orderMapper.toDto(order));
        order.setDeliveryPrice(deliveryPrice);

        return orderMapper.toDto(orderRepo.save(order));
    }

    public OrderDto assemblyOrder(UUID orderId) {
        log.debug("assembly order by id {}", orderId);
        Order order = orderRepo.getOrderById(orderId);
        warehouseClient.assemblyProductsForOrder(AssemblyProductRequest.builder()
                        .orderId(orderId)
                        .products(order.getProducts())
                .build());

        order.setState(OrderState.ASSEMBLED);
        Order savedOrder = orderRepo.save(order);
        return orderMapper.toDto(savedOrder);
    }

    public OrderDto assemblyOrderFailed(UUID orderId) {
        log.debug("assembly order failed by id {}", orderId);
        Order order = orderRepo.getOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        Order savedOrder = orderRepo.save(order);

        return orderMapper.toDto(savedOrder);
    }
}
