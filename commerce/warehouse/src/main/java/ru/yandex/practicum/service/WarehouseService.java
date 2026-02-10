package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.Address;
import ru.yandex.practicum.dao.Dimension;
import ru.yandex.practicum.dao.WarehouseProduct;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.repo.WarehouseProductRepo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseProductRepo warehouseProductRepo;

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

            volume += dimension.getDepth() * dimension.getWeight() * dimension.getHeight() * quantity;

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

    @Transactional(readOnly = true)
    public AddressDto getAddress() {
        String address = new Address().getAddress();
        return AddressDto.builder()
                .city(address)
                .country(address)
                .street(address)
                .home(address)
                .apartment(address)
                .build();
    }
}
