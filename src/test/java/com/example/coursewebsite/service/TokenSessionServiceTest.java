package com.example.coursewebsite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class TokenSessionServiceTest {

    @Test
    void saveActiveTokenStoresTokenSessionWithTtl() {
        StringRedisTemplate redisTemplate = org.mockito.Mockito.mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = org.mockito.Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        TokenSessionService tokenSessionService = new TokenSessionService(redisTemplate);

        tokenSessionService.saveActiveToken("token-1", "student", Duration.ofMinutes(30));

        verify(valueOperations).set("auth:token:token-1", "student", Duration.ofMinutes(30));
    }

    @Test
    void deleteActiveTokenRemovesTokenSession() {
        StringRedisTemplate redisTemplate = org.mockito.Mockito.mock(StringRedisTemplate.class);
        when(redisTemplate.delete("auth:token:token-1")).thenReturn(true);
        TokenSessionService tokenSessionService = new TokenSessionService(redisTemplate);

        tokenSessionService.deleteActiveToken("token-1");

        verify(redisTemplate).delete("auth:token:token-1");
    }

    @Test
    void isActiveReturnsTrueOnlyWhenTokenExistsForUsername() {
        StringRedisTemplate redisTemplate = org.mockito.Mockito.mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = org.mockito.Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("auth:token:token-1")).thenReturn("student");
        TokenSessionService tokenSessionService = new TokenSessionService(redisTemplate);

        assertThat(tokenSessionService.isActive("token-1", "student")).isTrue();
        assertThat(tokenSessionService.isActive("token-1", "teacher")).isFalse();
    }
}
