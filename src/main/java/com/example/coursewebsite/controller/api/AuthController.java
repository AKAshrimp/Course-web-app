package com.example.coursewebsite.controller.api;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.coursewebsite.dto.api.AuthResponse;
import com.example.coursewebsite.dto.api.LoginRequest;
import com.example.coursewebsite.dto.api.RegisterRequest;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.service.JwtTokenService;
import com.example.coursewebsite.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();
        String token = jwtTokenService.createToken(authentication.getName(), roles);
        User user = userService.getUserByUsername(authentication.getName())
                .orElseThrow(AuthUserNotFoundException::new);

        return toResponse(user, token);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = new User(
                request.username(),
                request.password(),
                request.fullName(),
                request.email(),
                request.phoneNumber()
        );
        User created = userService.registerNewUser(user);
        String token = jwtTokenService.createToken(created.getUsername(), List.copyOf(created.getRoles()));

        return toResponse(created, token);
    }

    @GetMapping("/me")
    public AuthResponse me(Principal principal) {
        if (principal == null) {
            throw new AuthUserNotFoundException();
        }

        User user = userService.getUserByUsername(principal.getName())
                .orElseThrow(AuthUserNotFoundException::new);
        return toResponse(user, null);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
    }

    private AuthResponse toResponse(User user, String token) {
        return new AuthResponse(
                token,
                user.getUsername(),
                user.getFullName(),
                List.copyOf(user.getRoles())
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private static class AuthUserNotFoundException extends RuntimeException {
    }
}
