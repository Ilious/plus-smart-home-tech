package ru.yandex.practicum.feign;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseOperations {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping
    void addNewProductToWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/add")
    void addProductQuantity(@RequestBody AddProductToWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkProductAvailability(@RequestBody ShoppingCartDto dto);

    @GetMapping("/address")
    AddressDto getAddress();
}
