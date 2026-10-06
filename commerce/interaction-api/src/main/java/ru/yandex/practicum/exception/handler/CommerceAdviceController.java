package ru.yandex.practicum.exception.handler;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.EntityNotEnoughException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.NotAuthorizedException;
import ru.yandex.practicum.exception.ServiceNotUpException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class CommerceAdviceController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotEnoughException.class)
    public ErrorResponse handleEntityNotEnoughException(EntityNotEnoughException ex, HttpServletRequest request) {
        log.error("Bad request to: {}", request.getRequestURI(), ex);

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad request error")
                .message("Resource not enough by the next path.")
                .path(request.getRequestURI())
                .build();
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        log.error("Entity not enough or not found: {}", request.getRequestURI(), ex);

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Entity not found error")
                .message("Entity not found by the next path.")
                .path(request.getRequestURI())
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAuthorizedException.class)
    public ErrorResponse handleServiceNotUp(NotAuthorizedException ex, HttpServletRequest request) {
        log.error("User not authorized: {}", ex.getMessage(), ex);

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("User not authorized error")
                .message("User can't be processed by the next path.")
                .path(request.getRequestURI())
                .build();
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceNotUpException.class)
    public ErrorResponse handleServiceNotUpException(ServiceNotUpException ex, HttpServletRequest request) {
        log.error("Fallback triggered for request to: {}", request.getRequestURI(), ex);

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("External Service Unavailable")
                .message("The required microservice is currently down. Fallback initiated.")
                .path(request.getRequestURI())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FeignException.class)
    public ErrorResponse handleFeignException(FeignException ex, HttpServletRequest request) {
        log.error("Feign call failed with status {}: {}", ex.status(), ex.getMessage(), ex);

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(ex.status() != -1 ? ex.status() : 500)
                .error("Remote Call Error")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleUnknownException(Exception ex, HttpServletRequest request) {
        log.error("Unknown exception: {}", ex.getMessage(), ex);

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(500)
                .error("Unknown Error")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}
