package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.Address;
import ru.yandex.practicum.dao.Dimension;
import ru.yandex.practicum.dao.OrderBooking;
import ru.yandex.practicum.dao.WarehouseProduct;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.EntityNotEnoughException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.repo.OrderBookingRepo;
import ru.yandex.practicum.repo.WarehouseProductRepo;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseProductRepo warehouseProductRepo;

    private final OrderBookingRepo orderBookingRepo;

    private final WarehouseProductMapper warehouseProductMapper;

    public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {
        log.debug("Add new product to warehouse: id {}", request.getProductId());

        warehouseProductRepo.save(warehouseProductMapper.toEntity(request));
    }

    public void addProductQuantity(AddProductToWarehouseRequest request) {
        log.debug("Add product quantity: id {}", request.getProductId());

        WarehouseProduct product = warehouseProductRepo.findById(request.getProductId())
                .orElseThrow(() ->
                        new EntityNotFoundException(String.valueOf(request.getProductId()), WarehouseProduct.class));
        product.setQuantity(product.getQuantity() + request.getQuantity());
    }

    @CacheEvict(cacheNames = "warehouse", allEntries = true)
    public BookedProductsDto checkProductAvailability(ShoppingCartDto dto) {
        log.debug("Check product availability, shopping cart id {}", dto.getShoppingCartId());
        Set<UUID> uuids = dto.getProducts().keySet();

        List<WarehouseProduct> allByProductIdIn = warehouseProductRepo.findAllByProductIdIn(uuids);

        Map<UUID, WarehouseProduct> products = allByProductIdIn.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        for (UUID productId : uuids)
            if (!products.containsKey(productId))
                throw new EntityNotFoundException(String.valueOf(productId), WarehouseProduct.class);

        double weight = 0, volume = 0;
        boolean fragile = false;

        for (Map.Entry<UUID, Long> productEntry : dto.getProducts().entrySet()) {
            UUID key = productEntry.getKey();
            Long quantity = productEntry.getValue();

            WarehouseProduct warehouseProduct = products.get(key);

            if (warehouseProduct.getQuantity() < quantity)
                throw new IllegalArgumentException(String.format("Not enough products with id %s in warehouse", key));

            warehouseProduct.setQuantity(warehouseProduct.getQuantity() - quantity);

            weight += warehouseProduct.getWeight() * quantity;

            Dimension dimension = warehouseProduct.getDimension();

            volume += dimension.getDepth() * dimension.getWidth() * dimension.getHeight() * quantity;

            if (warehouseProduct.getFragile())
                fragile = true;
        }

        warehouseProductRepo.saveAll(allByProductIdIn);

        return BookedProductsDto.builder()
                .deliveryWeight(weight)
                .deliveryVolume(volume)
                .fragile(fragile)
                .build();
    }

    @Cacheable(cacheNames = "warehouse_address")
    @Transactional(readOnly = true)
    public AddressDto getAddress() {
        String address = new Address().getAddress();
        return AddressDto.builder()
                .city(address)
                .country(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    public BookedProductsDto assemblyProductsForOrder(AssemblyProductRequest request) {
        log.debug("assembly products for order id {}", request.getOrderId());
        if (orderBookingRepo.findByOrderId(request.getOrderId()).isPresent())
            throw new IllegalArgumentException(String.format("Order already exists by id %s", request.getOrderId()));

        Map<UUID, Long> orderProducts = request.getProducts();

        validateProductsPresent(orderProducts);

        OrderBooking booking = OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(new HashMap<>(orderProducts))
                .build();

        reserveProductsForBooking(orderProducts);
        orderBookingRepo.save(booking);

        return calculateOrderDetails(orderProducts);
    }

    private BookedProductsDto calculateOrderDetails(Map<UUID, Long> orderProducts) {
        Map<UUID, WarehouseProduct> warehouseProducts = getWarehouseProducts(orderProducts.keySet());

        double totalWeight = 0;
        double totalVolume = 0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> cartProduct : orderProducts.entrySet()) {
            WarehouseProduct warehouseProduct = warehouseProducts.get(cartProduct.getKey());

            double productVolume = warehouseProduct.getDimension().getHeight() *
                    warehouseProduct.getDimension().getDepth() *
                    warehouseProduct.getDimension().getWidth();

            totalVolume += productVolume * cartProduct.getValue();
            totalWeight += warehouseProduct.getWeight() * cartProduct.getValue();

            if (warehouseProduct.getFragile()) {
                hasFragile = true;
            }
        }

        return BookedProductsDto.builder()
                .deliveryVolume(totalVolume)
                .deliveryWeight(totalWeight)
                .fragile(hasFragile)
                .build();
    }

    private void validateProductsPresent(Map<UUID, Long> products) {
        Map<UUID, WarehouseProduct> presentProducts = getWarehouseProducts(products.keySet());

        products.forEach((id, requestedQuantity) -> {
            WarehouseProduct warehouseProduct = presentProducts.get(id);

            if (warehouseProduct == null)
                throw new EntityNotFoundException(String.valueOf(id), WarehouseProduct.class);

            if (requestedQuantity > warehouseProduct.getQuantity()) {
                throw new IllegalArgumentException(String.format("Not enough products %d requested %d",
                        warehouseProduct.getQuantity(), requestedQuantity));
            }
        });
    }

    private Map<UUID, WarehouseProduct> getWarehouseProducts(Set<UUID> productIds) {
        return warehouseProductRepo.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
    }

    private void reserveProductsForBooking(Map<UUID, Long> orderProducts) {
        Map<UUID, WarehouseProduct> warehouseProducts = getWarehouseProducts(orderProducts.keySet());

        orderProducts.forEach((id, quantity) -> {
            WarehouseProduct product = warehouseProducts.get(id);

            if (product == null)
                throw new EntityNotFoundException(String.valueOf(id), WarehouseProduct.class);

            if (product.getQuantity() - quantity < 0) {
                throw new EntityNotEnoughException(String.valueOf(id), quantity, product.getQuantity(),
                        WarehouseProduct.class);
            }

            long newQuantity = product.getQuantity() - quantity;

            product.setQuantity(newQuantity);
            warehouseProductRepo.save(product);
        });
    }

    public void acceptReturn(Map<UUID, Long> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        Map<UUID, WarehouseProduct> warehouseProducts = getWarehouseProducts(products.keySet());

        products.forEach((id, returnedQuantity) -> {
            if (returnedQuantity <= 0) {
                log.warn("Incorrect quantity for return product id {} count {}", id, returnedQuantity);
                return;
            }

            WarehouseProduct product = warehouseProducts.get(id);

            if (product == null)
                throw new EntityNotFoundException(String.valueOf(id), WarehouseProduct.class);
            else {
                Long newQuantity = product.getQuantity() + returnedQuantity;
                product.setQuantity(newQuantity);
                warehouseProductRepo.save(product);
            }
        });
    }

    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking orderBooking = orderBookingRepo.findByOrderId(request.getOrderId())
                .orElseThrow(() ->
                        new EntityNotFoundException(String.valueOf(request.getOrderId()), OrderBooking.class));

        orderBooking.setDeliveryId(request.getDeliveryId());
    }
}
