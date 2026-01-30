package ru.yandex.practicum.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class ApiError {
    private final String message;
    private final int value;
}
