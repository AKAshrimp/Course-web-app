package com.example.coursewebsite.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenSessionService {

    private static final String TOKEN_KEY_PREFIX = "auth:token:";

    private final StringRedisTemplate redisTemplate;

    public TokenSessionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveActiveToken(String tokenId, String username, Duration ttl) {
        redisTemplate.opsForValue().set(tokenKey(tokenId), username, ttl);
    }

    public boolean isActive(String tokenId, String username) {
        return username.equals(redisTemplate.opsForValue().get(tokenKey(tokenId)));
    }

    public void deleteActiveToken(String tokenId) {
        redisTemplate.delete(tokenKey(tokenId));
    }

    private String tokenKey(String tokenId) {
        return TOKEN_KEY_PREFIX + tokenId;
    }
}
