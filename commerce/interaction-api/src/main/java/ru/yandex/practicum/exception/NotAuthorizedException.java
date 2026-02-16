package ru.yandex.practicum.exception;

public class NotAuthorizedException extends RuntimeException {

    public NotAuthorizedException(String username) {
        super(String.format("User is not authorized %s", username));
    }
}
