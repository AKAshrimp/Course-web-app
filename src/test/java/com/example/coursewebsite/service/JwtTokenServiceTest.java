package com.example.coursewebsite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

class JwtTokenServiceTest {

    private static final String SECRET = "local-dev-secret-local-dev-secret-local-dev-secret";

    @Test
    void createTokenCachesJwtIdWithMatchingTtl() {
        TokenSessionService tokenSessionService = org.mockito.Mockito.mock(TokenSessionService.class);
        JwtTokenService jwtTokenService = new JwtTokenService(jwtEncoder(), tokenSessionService, Duration.ofMinutes(30));

        String token = jwtTokenService.createToken("student", List.of("ROLE_STUDENT"));
        Jwt decoded = jwtDecoder().decode(token);

        ArgumentCaptor<String> tokenId = ArgumentCaptor.forClass(String.class);
        verify(tokenSessionService).saveActiveToken(tokenId.capture(), any(), any());
        verify(tokenSessionService).saveActiveToken(tokenId.getValue(), "student", Duration.ofMinutes(30));

        assertThat(decoded.getId()).isEqualTo(tokenId.getValue());
        assertThat(decoded.getSubject()).isEqualTo("student");
        assertThat(decoded.getClaimAsStringList("roles")).containsExactly("ROLE_STUDENT");
    }

    private JwtEncoder jwtEncoder() {
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
