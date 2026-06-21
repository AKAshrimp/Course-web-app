package com.example.coursewebsite.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.example.coursewebsite.service.JwtTokenService;
import com.example.coursewebsite.service.TokenSessionService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

class RedisJwtDecoderTest {

    private static final String SECRET = "local-dev-secret-local-dev-secret-local-dev-secret";

    @Test
    void decodeRejectsJwtWhenTokenSessionIsMissingFromRedis() {
        TokenSessionService tokenSessionService = org.mockito.Mockito.mock(TokenSessionService.class);
        JwtTokenService jwtTokenService = new JwtTokenService(jwtEncoder(), tokenSessionService, 120);
        String token = jwtTokenService.createToken("student", List.of("ROLE_STUDENT"));
        JwtDecoder decoder = new RedisJwtDecoder(jwtDecoder(), tokenSessionService);

        when(tokenSessionService.isActive(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq("student")))
                .thenReturn(false);

        assertThatThrownBy(() -> decoder.decode(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("inactive");
    }

    private NimbusJwtEncoder jwtEncoder() {
        SecretKey key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    private JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
