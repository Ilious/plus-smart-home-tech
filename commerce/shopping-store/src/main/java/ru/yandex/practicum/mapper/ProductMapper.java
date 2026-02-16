package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.dao.Product;
import ru.yandex.practicum.dto.shopping.store.ProductDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(source = "productCategory", target = "category")
    Product toEntity(ProductDto dto);

    @Mapping(source = "category", target = "productCategory")
    ProductDto toDto(Product entity);

    @Mapping(source = "productCategory", target = "category")
    Product updateFields(@MappingTarget Product entity, ProductDto dto);
}
