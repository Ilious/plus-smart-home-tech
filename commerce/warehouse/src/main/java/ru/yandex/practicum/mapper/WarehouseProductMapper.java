package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dao.WarehouseProduct;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseProductMapper {

    @Mapping(target = "dimension.width", source = "request.dimensionDto.width")
    @Mapping(target = "dimension.height", source = "request.dimensionDto.height")
    @Mapping(target = "dimension.depth", source = "request.dimensionDto.depth")
    @Mapping(target = "quantity", constant = "0L")
    WarehouseProduct toEntity(NewProductInWarehouseRequest request);

}
