package ru.yandex.practicum.utils;

import org.slf4j.Logger;

import java.util.Optional;

public class LogUtils {
    public static <T> Optional<T> logIfEmpty(Optional<T> optional, Logger log, String message, Object... args) {
        if (optional.isEmpty()) {
            log.warn(message, args);
        }
        return optional;
    }
}
