package com.example.coursewebsite.dto.api;

import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
        @NotNull Long courseId
) {
}
