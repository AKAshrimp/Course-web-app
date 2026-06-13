package com.example.coursewebsite.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.coursewebsite.dto.admin.AdminLoginRequest;
import com.example.coursewebsite.dto.admin.AdminLoginResponse;
import com.example.coursewebsite.service.JwtTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AdminAuthController(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public AdminLoginResponse login(@Valid @RequestBody AdminLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();

        if (!roles.contains("ROLE_TEACHER")) {
            throw new AdminAccessDeniedException();
        }

        String token = jwtTokenService.createToken(authentication.getName(), roles);
        return new AdminLoginResponse(token, authentication.getName(), "ROLE_TEACHER");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    private static class AdminAccessDeniedException extends RuntimeException {
    }
}