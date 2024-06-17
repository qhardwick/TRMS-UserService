package com.skillstorm.configs;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.jcache.JCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Objects.requireNonNull;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new JCacheCacheManager(requireNonNull(jCacheManagerFactory().getObject()));
    }

    @Bean
    public JCacheManagerFactoryBean jCacheManagerFactory() {
        JCacheManagerFactoryBean jCacheManagerFactoryBean = new JCacheManagerFactoryBean();
        jCacheManagerFactoryBean.setCacheManagerUri(null);
        jCacheManagerFactoryBean.setBeanClassLoader(getClass().getClassLoader());
        return jCacheManagerFactoryBean;
    }
}
