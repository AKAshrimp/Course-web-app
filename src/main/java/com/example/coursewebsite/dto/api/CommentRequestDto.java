package com.example.coursewebsite.dto.api;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto(
        @NotBlank(message = "Comment content is required")
        String content
) {
}
