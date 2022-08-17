package io.papermc.hangarauth.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String HASH_CACHE = "HASH_CACHE";
    public static final String GRAVATAR_CACHE = "GRAVATAR_CACHE";

}
