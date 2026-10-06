package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityNotEnoughException extends RuntimeException {

    public EntityNotEnoughException(String id, Long requested, Long total, Class<?> entityClass) {
        super(String.format("%s not enough by id %s requested %d total %d", entityClass.getName(), id, requested, total));
    }
}