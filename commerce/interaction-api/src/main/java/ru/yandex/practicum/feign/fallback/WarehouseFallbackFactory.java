package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.ServiceNotUpException;
import ru.yandex.practicum.feign.client.WarehouseClient;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class WarehouseFallbackFactory implements FallbackFactory<WarehouseClient> {

    @Override
    public WarehouseClient create(Throwable cause) {
        log.error("Warehouse link failed. Circuit opened due to: {}", cause.getMessage());

        return new WarehouseClient() {
            @Override
            public AddressDto getAddress() {
                throw new ServiceNotUpException("Warehouse address service is unavailable", cause);
            }

            @Override
            public void shippedToDelivery(ShippedToDeliveryRequest request) {
                log.error("Cannot shipped to delivery for order {}. Warehouse is down", request.getOrderId());
                throw new ServiceNotUpException("Shipped to delivery failed", cause);
            }

            @Override
            public void acceptReturn(Map<UUID, Long> products) {
                log.error("Cannot accept return for products {}. Warehouse is down", products.keySet());
                throw new ServiceNotUpException("Accept return failed", cause);
            }

            @Override
            public BookedProductsDto assemblyProductsForOrder(AssemblyProductRequest request) {
                log.error("Cannot assembly products for order {}. Warehouse is down", request.getProducts());
                throw new ServiceNotUpException("Assembly products for order failed", cause);
            }

            @Override
            public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {
                log.error("Cannot add new product to warehouse {}. Warehouse is down", request.getProductId());
                throw new ServiceNotUpException("Add new product to warehouse failed", cause);
            }

            @Override
            public void addProductQuantity(AddProductToWarehouseRequest request) {
                log.error("Cannot add product quantity for product {}. Warehouse is down", request.getProductId());
                throw new ServiceNotUpException("Add product quantity failed", cause);
            }

            @Override
            public BookedProductsDto checkProductAvailability(ShoppingCartDto dto) {
                log.error("Cannot check availability for Cart {}. Warehouse is down.", dto.getShoppingCartId());
                throw new ServiceNotUpException("Stock check failed", cause);
            }
        };
    }
}