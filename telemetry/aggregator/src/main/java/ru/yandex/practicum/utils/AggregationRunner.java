package ru.yandex.practicum.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.component.AggregationStarter;

@Component
@RequiredArgsConstructor
public class AggregationRunner implements CommandLineRunner {

    private final AggregationStarter aggregationStarter;

    @Override
    public void run(String... args) throws Exception {
        aggregationStarter.start();
    }
}
