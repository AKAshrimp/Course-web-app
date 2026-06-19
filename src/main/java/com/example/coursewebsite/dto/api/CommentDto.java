package com.example.coursewebsite.dto.api;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String content,
        String authorName,
        LocalDateTime createdAt
) {
}
