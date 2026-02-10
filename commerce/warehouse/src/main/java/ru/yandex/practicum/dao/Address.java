package ru.yandex.practicum.dao;

import lombok.*;

import java.security.SecureRandom;
import java.util.Random;


@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};

    private String country;

    private String city;

    private String street;

    private String home;

    private String apartment;

    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom())
            .nextInt(0, ADDRESSES.length)];

    public String getAddress() {
        return CURRENT_ADDRESS;
    }
}
