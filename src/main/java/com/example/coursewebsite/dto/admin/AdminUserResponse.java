package com.example.coursewebsite.dto.admin;

import java.util.Set;

public record AdminUserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phoneNumber,
        Set<String> roles
) {
}