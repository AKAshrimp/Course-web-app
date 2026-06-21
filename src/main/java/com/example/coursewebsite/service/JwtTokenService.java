package com.example.coursewebsite.service;

import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final TokenSessionService tokenSessionService;
    private final Duration tokenTtl;

    @Autowired
    public JwtTokenService(
            JwtEncoder jwtEncoder,
            TokenSessionService tokenSessionService,
            @Value("${app.jwt.ttl-minutes:120}") long tokenTtlMinutes) {
        this(jwtEncoder, tokenSessionService, Duration.ofMinutes(tokenTtlMinutes));
    }

    JwtTokenService(JwtEncoder jwtEncoder, TokenSessionService tokenSessionService, Duration tokenTtl) {
        this.jwtEncoder = jwtEncoder;
        this.tokenSessionService = tokenSessionService;
        this.tokenTtl = tokenTtl;
    }

    public String createToken(String username, List<String> roles) {
        Instant now = Instant.now();
        String tokenId = UUID.randomUUID().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("coursewebsite")
                .id(tokenId)
                .issuedAt(now)
                .expiresAt(now.plus(tokenTtl))
                .subject(username)
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        tokenSessionService.saveActiveToken(tokenId, username, tokenTtl);
        return token;
    }
}