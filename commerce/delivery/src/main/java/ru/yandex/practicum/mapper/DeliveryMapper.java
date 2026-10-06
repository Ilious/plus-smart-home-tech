package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.dao.Delivery;
import ru.yandex.practicum.dto.delivery.DeliveryDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeliveryMapper {

    DeliveryDto toDto(Delivery delivery);

    @Mapping(target = "fromAddress.addressId", ignore = true)
    @Mapping(target = "toAddress.addressId", ignore = true)
    @Mapping(target = "deliveryState", constant = "CREATED")
    Delivery toEntity(DeliveryDto deliveryDto);
}
