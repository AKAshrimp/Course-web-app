package com.example.coursewebsite.dto.admin;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminUserRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        String password,
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Email @Size(max = 100) String email,
        @Size(max = 30) String phoneNumber,
        Set<String> roles
) {
}