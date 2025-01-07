package com.skillstorm.configs;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.skillstorm.dtos.DepartmentDto;
import com.skillstorm.services.DepartmentService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager departmentCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("departmentCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(10));
        cacheManager.setAsyncCacheMode(true);
        return cacheManager;
    }

    @Bean
    public AsyncLoadingCache<String, DepartmentDto> departmentDtoCache(DepartmentService departmentService) {
        return Caffeine.newBuilder()
                .executor(Executors.newFixedThreadPool(1))
                .buildAsync((key, executor) -> loadDepartmentDto(key, departmentService));
    }

    private CompletableFuture<DepartmentDto> loadDepartmentDto(String key, DepartmentService departmentService) {
        return CompletableFuture.supplyAsync(() -> departmentService.findByName(key))
                .thenCompose(Mono::toFuture);
    }

}
