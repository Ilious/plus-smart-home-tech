package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dao.WarehouseProduct;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseProductMapper {

    WarehouseProduct toEntity(NewProductInWarehouseRequest request);

}
