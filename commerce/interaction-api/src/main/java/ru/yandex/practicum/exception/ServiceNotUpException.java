package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceNotUpException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "service is not up";

    public ServiceNotUpException(String warehouseAddressServiceIsUnavailable, Throwable cause) {
        super(warehouseAddressServiceIsUnavailable, cause);
    }
}
