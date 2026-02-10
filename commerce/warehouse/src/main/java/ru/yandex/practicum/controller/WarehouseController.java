package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.feign.WarehouseOperations;
import ru.yandex.practicum.service.WarehouseService;

@Validated
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
}
