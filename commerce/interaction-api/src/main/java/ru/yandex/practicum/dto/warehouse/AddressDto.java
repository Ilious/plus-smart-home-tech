package ru.yandex.practicum.dto.warehouse;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private String country;

    private String city;

    private String street;

    private String home;

    private String apartment;
}
