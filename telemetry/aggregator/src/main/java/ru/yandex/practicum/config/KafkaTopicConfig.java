package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@ConfigurationProperties(prefix = "server.kafka.topic-config")
@Component
public class KafkaTopicConfig {

    private String hubs;

    private String sensors;

    private String snapshots;
}
