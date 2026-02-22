package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.dao.Order;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    @Mappings({
            @Mapping(target = "shoppingCartId", source = "dto.shoppingCart.shoppingCartId"),
            @Mapping(target = "products", source = "dto.shoppingCart.products"),
            @Mapping(target = "state", constant = "NEW")
    })
    Order toEntity(CreateNewOrderRequest dto);

    OrderDto toDto(Order entity);
}
