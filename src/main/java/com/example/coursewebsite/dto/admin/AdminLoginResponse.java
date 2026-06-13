package com.example.coursewebsite.dto.admin;

public record AdminLoginResponse(
        String token,
        String username,
        String role
) {
}