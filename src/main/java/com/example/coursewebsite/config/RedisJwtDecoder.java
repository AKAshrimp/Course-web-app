package com.example.coursewebsite.config;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import com.example.coursewebsite.service.TokenSessionService;

public class RedisJwtDecoder implements JwtDecoder {

    private final JwtDecoder delegate;
    private final TokenSessionService tokenSessionService;

    public RedisJwtDecoder(JwtDecoder delegate, TokenSessionService tokenSessionService) {
        this.delegate = delegate;
        this.tokenSessionService = tokenSessionService;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        Jwt jwt = delegate.decode(token);
        if (!tokenSessionService.isActive(jwt.getId(), jwt.getSubject())) {
            throw new JwtException("JWT token is inactive");
        }
        return jwt;
    }
}
