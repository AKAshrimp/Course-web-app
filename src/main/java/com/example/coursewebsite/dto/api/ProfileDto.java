package com.example.coursewebsite.dto.api;

import java.util.List;

public record ProfileDto(
        String username,
        String fullName,
        String email,
        String phoneNumber,
        List<String> roles
) {
}
