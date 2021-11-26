package com.galvanize.prodman;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.TimeUnit;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class ProdmanApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProdmanApplication.class, args);
    }

    @Bean
    CacheManager cacheManager() {
        final CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache(
                "quotes",
                Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build());
        return cacheManager;
    }
}
