package ru.yandex.practicum.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String id, Class<?> entityClass) {
        super(String.format("%s with id %s not found", entityClass.getName(), id));
    }

    public EntityNotFoundException(String value, String field, Class<?> entityClass) {
        super(String.format("%s with %s %s not found", entityClass.getName(), field, value));
    }
}
