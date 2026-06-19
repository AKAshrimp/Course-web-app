package com.example.coursewebsite.dto.admin;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminPollRequest(
        @NotBlank(message = "Poll question is required")
        @Size(max = 255, message = "Poll question must not exceed 255 characters")
        String question,

        List<@NotBlank(message = "Poll option is required") @Size(max = 150) String> options
) {
}
