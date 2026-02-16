package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.dao.ShoppingCart;
import ru.yandex.practicum.dto.shopping.cart.ShoppingCartDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShoppingCartMapper {

    ShoppingCart toEntity(ShoppingCartDto dto);

    ShoppingCartDto toDto(ShoppingCart entity);

    ShoppingCart updateFields(@MappingTarget ShoppingCart entity, ShoppingCartDto dto);
}
