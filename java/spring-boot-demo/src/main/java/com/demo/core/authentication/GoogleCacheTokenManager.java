package com.demo.core.authentication;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 默认的token处理器
 * 这里使用的google cache做缓存
 * 可替代为redis jwt或者其他
 */
@Slf4j
public class GoogleCacheTokenManager implements TokenManager {

    // 创建一个 缓存，并定义缓存超时策略为 最后一次访问后超过15分钟后过期
    private static final Cache<String, Authentication> TokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .removalListener((RemovalListener<String, Authentication>) notification -> log.info("remove cause{}:{}", notification.getKey(), notification.getCause()))
            .build();


    public String generateToken(Authentication authentication) {
        String token = UUID.randomUUID().toString();
        TokenCache.put(token, authentication);
        return token;
    }


    public void removeToken(String token) {
        TokenCache.invalidate(token);
    }


    public Authentication getAuthenticationByToken(String token) {
        Authentication authentication = TokenCache.getIfPresent(token);
        return authentication;
    }


    public void delayExpired(String token) {
        //google cache 在之前获取时自动续期，这里不需要再调用逻辑
    }
}
