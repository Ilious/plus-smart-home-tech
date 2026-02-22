package ru.yandex.practicum.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Value("${app.cache.default-ttl-min:10}")
    public int defaultTtl;

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager caffeineCacheManager = new SimpleCacheManager();
        caffeineCacheManager.setCaches(Arrays.asList(
                buildCaffeineConfig("sensors", 1000, defaultTtl),
                buildCaffeineConfig("scenarios", 500, 60)
        ));
        return caffeineCacheManager;
    }

    private CaffeineCache buildCaffeineConfig(String name, int maxRecords, int maxTtl) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(maxTtl, TimeUnit.MINUTES)
                .maximumSize(maxRecords)
                .recordStats()
                .build());
    }
}
