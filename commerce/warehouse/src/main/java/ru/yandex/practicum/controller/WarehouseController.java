package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.feign.operation.WarehouseOperations;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${app.api-version}" + "/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseOperations {

    private final WarehouseService warehouseService;

    @Override
    public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {
        warehouseService.addNewProductToWarehouse(request);
    }

    @Override
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        warehouseService.addProductQuantity(request);
    }

    @Override
    public BookedProductsDto checkProductAvailability(ShoppingCartDto dto) {
        return warehouseService.checkProductAvailability(dto);
    }

    @Override
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        warehouseService.shippedToDelivery(request);
    }

    @Override
    public void acceptReturn(Map<UUID, Long> products) {
        warehouseService.acceptReturn(products);
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductRequest request) {
        return warehouseService.assemblyProductsForOrder(request);
    }
}
